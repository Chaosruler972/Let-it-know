$( document ).ready(function() 
{
   page_module.build_HTML();
   setTimeout(function() 
   {
		if(firebase.auth().currentUser != null)
		{
			window.location = "../admin_page/admin.html";
		}
   },500);
   page_module.auto_fill_buttons();
   
});
var page_module = (function () 
{
	const send_data = function() // handles the login function
	{
		let email = document.getElementById("email").value;
		email+="@gmail.com"; // appends email "ending" for every user, since DB is actually email based
		let pw = document.getElementById("password").value;
		
		if(email != null && pw != null)
		{
			firebase.auth().signInWithEmailAndPassword(email, pw).catch(function(error) // attempts to sign in
			{
				console.log(error.message);
				if (errorCode === 'auth/wrong-password') 
				{
					alert('Wrong password.');
				}
			}).then(function()
			{
				if(firebase.auth().currentUser != null) // upon success
					window.location = "../admin_page/admin.html";
			});
		}
		
		return false; // crucial to prevent refreshing page 
	};


	const build_HTML = function() // builds the form
	{
		let topbtndiv = document.createElement("div");
		topbtndiv.id = "top_button_div";
		topbtndiv.className = "top_button_div";
		document.body.appendChild(topbtndiv);
		
		let form_div = document.createElement("div");
		form_div.id = "form_div";
		form_div.className = "form_div";
		document.body.appendChild(form_div);
		
		
		const back_button_build = function()
		{
			let link = document.createElement("a"); // back to calendar button
			//var txt= document.createTextNode("back to calendar");
			let pic = document.createElement("IMG");
			pic.src = "../buttons/back.png";
			pic.height = 40;
			pic.width = 40;
			link.appendChild(pic);
			link.href = "../index.html";
			topbtndiv.appendChild(link);
		};
		
		const login_form_build = function()
		{
			let form_obj = document.createElement("FORM");
			form_obj.id = "form";
			form_obj.autocomplete = "true"; // turns on auto complete on form
			form_obj.acceptCharset = "UTF-8"; // utilizes english-only with special characters incl @
			form_obj.onsubmit = send_data;
			
			
			let email = document.createElement("INPUT"); // username field
			email.setAttribute("type", "input");
			email.name = "email";
			email.required = "true";
			email.autocomplete = "true";
			email.form = form_obj;
			email.id = "email";
			email.setAttribute("pattern","[a-zA-Z0-9!#$%^*_|]{0,100}"); // username regex
			
			form_obj.appendChild(document.createTextNode("E-mail: "));
			form_obj.appendChild(email);
			
			let pw = document.createElement("INPUT"); // password field
			pw.setAttribute("type","password");
			pw.name = "password";
			pw.required = "true";
			pw.autocomplete = "true";
			pw.form = form_obj;
			pw.id = "password";
			
			form_obj.appendChild(document.createElement("br"));
			form_obj.appendChild(document.createTextNode("Password: "));
			form_obj.appendChild(pw);
			
			
			let btn = document.createElement("button"); // submit button
			btn.form = form_obj;
			btn.formNoValidate = false; // validate data
			btn.type = "submit";
			btn.name = "submit";
			btn.innerHTML = "Submit";
			
			
			let btn_reset = document.createElement("button"); // reset fields button
			btn_reset.form = form_obj;
			btn_reset.formNoValidate = false;
			btn_reset.type = "reset";
			btn_reset.name = "reset";
			btn_reset.innerHTML = "Reset";
			
			form_obj.appendChild(document.createElement("br"));
			form_obj.appendChild(btn);
			form_obj.appendChild(document.createTextNode(" "));
			form_obj.appendChild(btn_reset);
			
			form_div.appendChild(form_obj);
			document.getElementById('email').onkeydown = function(e) // if you press enter it is the same as clicking "submit"
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

	const auto_fill_buttons = function() // temproary auto fill buttons for development stage
	{
		// function for testing purposes
		// autofill button function
		
		let btn1 = document.createElement("button");
		btn1.type = "button";
		btn1.name = "fill_user";
		btn1.appendChild(document.createTextNode("Fill user (company) reguler"));
		btn1.addEventListener("click",function() 
		{
			document.getElementById("email").value = "terem";
			document.getElementById("password").value = "123456";
		});
		
		let btn2 = document.createElement("button");
		btn2.type = "button";
		btn2.name = "Fill admin";
		btn2.appendChild(document.createTextNode("Fill admin (nadav)"));
		btn2.addEventListener("click",function() 
		{
			document.getElementById("email").value = "yeseg11";
			document.getElementById("password").value = "123456";
		});
		
		document.body.appendChild(btn1);
		document.body.appendChild(btn2);
	};
	
	return {build_HTML,auto_fill_buttons,};

}());
