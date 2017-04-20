$( document ).ready(function() 
{
    buttons_insert();
	createTable();
	db_rendering();
	
});

let db_rendering = function()
{
	let i = 1;
	let table = document.getElementById("table"); 
	let is_admin = 0;
	let admin_req = firebase.database();
	admin_req = admin_req.ref("admin");
	admin_req.once('value').then(function(snapshot)
	{
		if(snapshot.val().localeCompare(firebase.auth().currentUser.email)==0)
		{
			is_admin=1;
			let move_mail = function()
			{
				let new_email = document.getElementById("email").value;
				let admin_req = firebase.database();
				admin_req = admin_req.ref("admin");
				admin_req.set(new_email);
			};
			
			let form_obj = document.createElement("FORM");
			form_obj.id = "form";
			form_obj.autocomplete = "false";
			form_obj.acceptCharset = "UTF-8";
			form_obj.onsubmit = move_mail;
			
			let email = document.createElement("INPUT");
			email.setAttribute("type", "email");
			email.name = "email";
			email.required = "true";
			email.autocomplete = "false";
			email.form = form_obj;
			email.id = "email";
			
			let btn = document.createElement("button");
			btn.form = form_obj;
			btn.formNoValidate = false; // validate data
			btn.type = "submit";
			btn.name = "submit";
			btn.innerHTML = "Make that e-mail admin!";
			
			form_obj.appendChild(document.createTextNode("Move admin to another e-mail: "));
			form_obj.appendChild(email);
			form_obj.appendChild(btn);
			document.body.appendChild(document.createElement("br"));
			document.body.appendChild(form_obj);
		}
	});
	
	let database = firebase.database();
	let leadsRef = database.ref('Facebook');
	leadsRef.on('value', function(snapshot) 
	{
		snapshot.forEach(function(childSnapshot) 
		{
			let email = childSnapshot.val().substring(1,childSnapshot.val().length);
			if(is_admin==1 || email.localeCompare(firebase.auth().currentUser.email) == 0)
			{
				let status = "";
				if(childSnapshot.val().charAt(0) == '0')
					status = "Not approved yet";
				else
					status = "Approved";
				let row = table.insertRow(i);
				let cell1 = row.insertCell(0);
				let cell2 = row.insertCell(1);
				if(status == "Approved")
					cell2.innerHTML = status;
				else
				{
					if(is_admin == 1)
					{
						let aprvbtn = document.createElement('input');
						aprvbtn.type = "button";
						aprvbtn.setAttribute("onClick","approve("+ '"' + childSnapshot.key+ '"' +");");
						aprvbtn.value = status;
						cell2.appendChild(aprvbtn);
					}
					else
					{
						cell2.innerHTML = status;
					}
				}
				let cell3 = document.createTextNode(email);
				row.appendChild(cell3);
				let cell4 = document.createElement('input');
				cell4.type = "button";
				cell4.setAttribute("onClick","del("+ '"' + childSnapshot.key+ '"' +");");
				cell4.value = "Delete";
				row.appendChild(cell4);
				cell1.innerHTML = "<a href='https://www.facebook.com/" +childSnapshot.key+ "'>" + childSnapshot.key + "</a>";
				i++;
			}
		});
	});
}


let createTable = function()
{
	let table = document.createElement("table");
	table.id = "table";
	document.body.appendChild(table);
	let row = table.insertRow(0);
	let cell1 = row.insertCell(0);
	let cell2 = row.insertCell(1);
	let cell3 = row.insertCell(2);
	let cell4 = row.insertCell(3);
	let cell5 = row.insertCell(4);
	cell1.innerHTML = "Facebook page name";
	cell2.innerHTML = "Status"
	cell3.innerHTML = "By";
	cell4.innerHTML = "Delete";
	cell5.innerHTML = "<input type='button' onclick='Add_new()' value='Add new Facebook page'/>";
};

let Add_new = function()
{
	let page = window.prompt("Enter a facebook page id (page id is viewable in format of https://www.facebook.com/{page-id} on any page)","");
	
	if(page == "" || page == null)
	{
		
	}
	else
	{
		let rootRef = firebase.database().ref();
		
		//if(page.charAt(page.length-1) == '/')  // HTML LINK breaker, decrepted
		//	page = page.substr(0,page.length-1);
		let page_unicoded = page.replace(/[^\x20-\x7E]+/g, '');
		if(page.localeCompare(page_unicoded) != 0) // meaning page had some special unicoded characters! 
			page = page.replace(/\D/g,''); // strips all non-numbers from page-id, leading to verifiable string
		let storesRef = rootRef.child('Facebook/' + page);
		storesRef.set("0" + firebase.auth().currentUser.email);
		location.reload();
	}

};

let del = function(key)
{
	let rootRef = firebase.database().ref();
	let storesRef = rootRef.child('Facebook/' + key);
	storesRef.remove();
	location.reload();
};

let approve = function(key)
{
	let rootRef = firebase.database().ref();
	let storesRef = rootRef.child('Facebook/' + key);
	storesRef.once('value').then(function(snapshot)
	{
		storesRef.set("1" + snapshot.val().substring(1,snapshot.val().length));
	});
	location.reload();
};

let buttons_insert = function()
{
    let link = document.createElement("a");
    //var txt= document.createTextNode("back to calendar");
	let backpic = document.createElement("IMG");
	backpic.src = "../buttons/back.png";
	backpic.height = 40;
	backpic.width = 40;
    link.appendChild(backpic);
    link.href = "../index.html";
    document.body.appendChild(link);
	
	
	let btn = document.createElement("button");
	btn.type = "button";
	btn.name = "log out";
	let log_out_pic = document.createElement("IMG");
	log_out_pic.src = "../buttons/log_out.png";
	log_out_pic.height = 40;
	log_out_pic.width = 40;
	btn.appendChild(log_out_pic);
	btn.addEventListener("click", function() { firebase.auth().signOut(); window.location = "../index.html"; });
	document.body.appendChild(btn);
};
