$( document ).ready(function() 
{
   page_module.build_HTML();
   
});
var page_module = (function () 
{
	const send_data = function() // sends form to db
	{
		let email = document.getElementById("email").value;
		let pw = document.getElementById("password").value;
		let pw2 = document.getElementById("password2").value;
		let colors = document.getElementById("colors").style.backgroundColor.split(',');
		let nickname = document.getElementById("name").value;
		let r = parseInt(colors[0].substring(4)).toString(16) + ""; // grabs color string
		let g = parseInt(colors[1]).toString(16)+ "";
		let b = parseInt(colors[2]).toString(16)+ "";
		if(r.length == 1) // fixes it to be 2 digit 
			r = "0" + r;
		if(g.length == 1)
			g = "0" + g;
		if(b.length == 1)
			b = "0" + b;
		let color_string = r+""+g+""+b;
		
		if(pw!=pw2) // tests password matching
		{
			alert("Passwords do not match");
			return false;
		}
		let admin_email = "";
		let admin_pw = "";
		const rest_of = function() // rest of create user function
		{
			let admin_req = firebase.database();
			admin_req = admin_req.ref("admin");
			admin_req.once('value').then(function(snapshot) // checks you are admin before adding the new user
			{
				
				if(firebase.auth().currentUser.email == snapshot.val() + "@gmail.com" ) 
				{
					let backup = firebase.auth().currentUser;
					firebase.auth().createUserWithEmailAndPassword(email+"@gmail.com", pw).catch(function(error) // creates username
					{
						alert(error);
					}).then(function()
					{
						let storesRef = firebase.database().ref().child('Users/' + email); // adds user color
						let obj = {color: color_string, url: " "};
						storesRef.set(obj);
						firebase.database().ref().child("Names/" + email).set(nickname); // sets new nickname
						firebase.database().ref().child("Crds/" + email).set(pw); // sets creditinals 
						firebase.database().ref().child("Auto_approve/" + email).set(0); // sets auto approve to false (default)
						firebase.auth().signInWithEmailAndPassword(admin_email, admin_pw).catch(function(error) // signs in as admin
						{
									console.log(error.message);
						}).then(function()
						{
							alert("Success! moving to main page");
							window.location = "../index.html";
							return false;
						});

					});
				}
				else
					alert("User doesn't have permission to add another user!");
			});
		};
		firebase.database().ref().child("Crds").once('value').then(function(snapshot) // grans admin creditinals
		{
			snapshot.forEach(function(childSnapshot) 
			{
				if(firebase.auth().currentUser.email == snapshot.val() + "@gmail.com" )
				{
					admin_email += firebase.auth().currentUser.email;
					admin_pw += childSnapshot.val();
				}
			});
		}).then(rest_of); // before actually trying to add user
		return false; // crucial to prevent refreshing upon submit
	};


	const build_HTML = function()
	{
		let btndiv = document.createElement("div"); 
		btndiv.id = "top_buttons_div";
		btndiv.className = "top_buttons_div";
		
		let formdiv = document.createElement("div");
		formdiv.id = "form_div";
		formdiv.className = "form_div";
		
		document.body.appendChild(btndiv);
		document.body.appendChild(formdiv);
		
		
		const back_button_build = function() //
		{
			let link = document.createElement("a");
			//var txt= document.createTextNode("back to calendar");
			let pic = document.createElement("IMG");
			pic.src = "../buttons/back.png";
			pic.height = 40;
			pic.width = 40;
			link.appendChild(pic);
			link.href = "../index.html";
			btndiv.appendChild(link);
		};
		
		const login_form_build = function()
		{
			let form_obj = document.createElement("FORM");
			form_obj.id = "form";
			form_obj.autocomplete = "true"; // turns on auto complete on form
			form_obj.acceptCharset = "UTF-8"; // utilizes english-only with special characters incl @
			form_obj.onsubmit = send_data;
			
			let email = document.createElement("INPUT");
			email.setAttribute("type", "input");
			email.name = "email";
			email.required = "true";
			email.autocomplete = "true";
			email.form = form_obj;
			email.id = "email";
			email.setAttribute("pattern","[a-zA-Z0-9!#$%^*_|]{0,100}"); // regex
			
			
			form_obj.appendChild(document.createTextNode("Username: "));
			form_obj.appendChild(email);
			
			let name = document.createElement("INPUT");
			name.setAttribute("type", "input");
			name.name = "name";
			name.required = "true";
			name.autocomplete = "true";
			name.form = form_obj;
			name.id = "name";
			
			form_obj.appendChild(document.createElement("br"));
			form_obj.appendChild(document.createTextNode("nickname (required): "));
			form_obj.appendChild(name);
			
			
			
			let pw = document.createElement("INPUT");
			pw.setAttribute("type","password");
			pw.name = "password";
			pw.required = "true";
			pw.autocomplete = "true";
			pw.form = form_obj;
			pw.id = "password";
			pw.minLength = 6;
		//	pw.setAttribute('min',6);
			
			form_obj.appendChild(document.createElement("br"));
			form_obj.appendChild(document.createTextNode("Password: "));
			form_obj.appendChild(pw);
			
			
			let pw2 = document.createElement("INPUT");
			pw2.setAttribute("type","password");
			pw2.name = "password2";
			pw2.required = "true";
			pw2.autocomplete = "true";
			pw2.form = form_obj;
			pw2.id = "password2";
			pw2.minLength = 6;
			
			form_obj.appendChild(document.createElement("br"));
			form_obj.appendChild(document.createTextNode("Confirm password: "));
			form_obj.appendChild(pw2);
			
			let btn = document.createElement("button");
			btn.form = form_obj;
			btn.formNoValidate = false; // validate data
			btn.type = "submit";
			btn.name = "submit";
			btn.innerHTML = "Submit";
			
			
			let btn_reset = document.createElement("button");
			btn_reset.form = form_obj;
			btn_reset.formNoValidate = false;
			btn_reset.type = "reset";
			btn_reset.name = "reset";
			btn_reset.innerHTML = "Reset";
			
			let color_btn = document.createElement("INPUT");
			var picker = new jscolor(color_btn);
			picker.valueElement = "";
			picker.fromString("#F0F0F0");
			color_btn.value = "Click";
			color_btn.size = 2;
			color_btn.id = "colors";
			color_btn.style.backgroundColor = "#F0F0F0";
			
			form_obj.appendChild(document.createElement("br"));
			let label = document.createTextNode("Color: ");
			form_obj.appendChild(label);
			form_obj.appendChild(color_btn);
			form_obj.appendChild(document.createElement("br"));
			form_obj.appendChild(btn);
			form_obj.appendChild(document.createTextNode(" "));
			form_obj.appendChild(btn_reset);
			
			formdiv.appendChild(form_obj);
			document.getElementById('email').onkeydown = function(e) // press enter to submit
			{
			   if(e.keyCode == 13){
				 send_data();
			   }
			};
			document.getElementById('password').onkeydown = function(e)
			{
			   if(e.keyCode == 13){
				 send_data();
			   }
			};
			
		};
		
		
		back_button_build();
		login_form_build();
	};
	
	return {build_HTML,};

}());	

