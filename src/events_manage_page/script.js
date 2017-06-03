$( document ).ready(function() 
{
    buttons_insert();
	createTable();
	db_rendering();
	
});
let factor = 0;

let db_rendering = function()
{
	let i = 1;
	let table = document.getElementById("table"); 
	let is_admin = 0;
	let admin_req = firebase.database();
	admin_req = admin_req.ref("admin");
	admin_req.once('value').then(function(snapshot)
	{
		if(firebase.auth().currentUser.email == snapshot.val() + "@gmail.com" )
		{
			is_admin=1;
			
			
			let manage_users_btn = document.createElement("button");
			manage_users_btn.type = "button";
			manage_users_btn.className = "manage_users_btn";
			manage_users_btn.innerHTML = "Manage users";
			manage_users_btn.addEventListener("click",function() 
			{
				window.location = "../manage_users/manage_users.html";
			});
			document.getElementById("top_buttons_div").appendChild(manage_users_btn);
		}
	});
	
	let database = firebase.database();
	let leadsRef = database.ref('Facebook');
	leadsRef.once('value').then(function(snapshot) 
	{
		snapshot.forEach(function(childSnapshot) 
		{
			let email = childSnapshot.val().substring(1,childSnapshot.val().length);
			if(is_admin==1 || firebase.auth().currentUser.email == email + "@gmail.com" )
			{
				let status = "";
				if(childSnapshot.val().charAt(0) == '0')
					status = "Pending";
				else
					status = "Approved";
				let row = table.insertRow(i);
				row.id = i;
				let cell1 = row.insertCell(0);
				let cell2 = row.insertCell(1);
				if(status == "Approved")
					cell2.innerHTML = status.fontcolor("green");
				else
				{
					if(is_admin == 1)
					{
						let aprvbtn = document.createElement('input');
						aprvbtn.type = "button";
						aprvbtn.style.backgroundColor = "orange";
						aprvbtn.setAttribute("onClick","approve("+ '"' + childSnapshot.key+ '"'+"," + i +");");
						aprvbtn.value = status;
						cell2.appendChild(aprvbtn);
					}
					else
					{
						cell2.innerHTML = status.fontcolor("orange");
					}
				}
				
				firebase.database().ref("Names/").once('value',function(snap)
				{
					snap.forEach(function(shot)
					{
						if(shot.key == email)
						{
							let cell3 = document.createTextNode(shot.val());
							row.insertCell(2).appendChild(cell3);
							let cell4 = document.createElement('input');
							cell4.type = "button";
							cell4.setAttribute("onClick","del("+ '"' + childSnapshot.key+ '"'+","+i +");");
							cell4.value = "Delete";
							row.insertCell(3).appendChild(cell4);
							cell1.innerHTML = "<a href='https://www.facebook.com/" +childSnapshot.key+ "'>" + childSnapshot.key + "</a>";
							i++;
						}
					});
				});
				
				
			}
		});
	});
}


let createTable = function()
{
	let table = document.createElement("table");
	table.id = "table";
	table.style.width = "100%";
	let div = document.createElement("div");
	div.id = "table_div";
	div.className = "table_div" ; 
	document.body.appendChild(div);
	div.appendChild(table);
	let row = table.insertRow(0);
	row.id = "row0";
	let cell1 = row.insertCell(0);
	let cell2 = row.insertCell(1);
	let cell3 = row.insertCell(2);
	let cell4 = row.insertCell(3);
//	let cell5 = row.insertCell(4);
	cell1.innerHTML = "Facebook page name";
	cell2.innerHTML = "Status"
	cell3.innerHTML = "By";
	cell4.innerHTML = "<input type='button' onclick='Add_new()' value='Add new Facebook page' style='width:100%'/>";
	
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
		let is_admin = 0;
		let admin_req = firebase.database();
		admin_req = admin_req.ref("admin");
		admin_req.once('value').then(function(snapshot)
		{
			if(firebase.auth().currentUser.email == snapshot.val() + "@gmail.com" )
			{
				is_admin = 1;
			}
			let autoapprove = 0;
			firebase.database().ref("Auto_approve").once('value', function(snapshot)
			{
				snapshot.forEach(function(ChildSnapshot)
				{
					
					if(ChildSnapshot.key+"@gmail.com" == firebase.auth().currentUser.email)
						autoapprove = ChildSnapshot.val();
				});
			}).then(function()
			{
				//if(page.charAt(page.length-1) == '/')  // HTML LINK breaker, decrepted
					//	page = page.substr(0,page.length-1);
					let page_unicoded = page.replace(/[^\x20-\x7E]+/g, '');
					if(page.localeCompare(page_unicoded) != 0) // meaning page had some special unicoded characters! 
						page = page.replace(/\D/g,''); // strips all non-numbers from page-id, leading to verifiable string
					let storesRef = rootRef.child('Facebook/' + page);
					storesRef.set(autoapprove + firebase.auth().currentUser.email.substring(0,firebase.auth().currentUser.email.indexOf("@")));
					let row = document.getElementById("table").insertRow(document.getElementById("table").childElementCount);
					row.id = document.getElementById("table").childElementCount;
					let cell1 = row.insertCell(0);
					let cell2 = row.insertCell(1);
					let cell3 = row.insertCell(2);
					cell1.innerHTML = "<a href='https://www.facebook.com/" +page+ "'>" + page + "</a>";
					let status = "Pending";
					if(autoapprove == 1)
					{
						let approved_status = "approved";
						cell2.innerHTML = approved_status.fontcolor("green");
					}
					else
					{
						if(is_admin == 1)
						{
							let aprvbtn = document.createElement('input');
							aprvbtn.type = "button";
							aprvbtn.style.backgroundColor = "orange";
							aprvbtn.setAttribute("onClick","approve("+ '"' + page+ '"'+"," + document.getElementById("table").childElementCount +");");
							aprvbtn.value = status;
							cell2.appendChild(aprvbtn);
						}
						else
						{
							cell2.innerHTML = status.fontcolor("red");
						}
					}
					cell3.innerHTML = firebase.auth().currentUser.email.substring(0,firebase.auth().currentUser.email.indexOf("@"));
					let cell4 = document.createElement('input');
					cell4.type = "button";
					cell4.setAttribute("onClick","del("+ '"' + page+ '"'+","+ document.getElementById("table").childElementCount +");");
					cell4.value = "Delete";
					row.insertCell(3).appendChild(cell4);
			});
			
		});
	}

};

let del = function(key,i)
{
	let rootRef = firebase.database().ref();
	let storesRef = rootRef.child('Facebook/' + key);
	storesRef.remove();
	document.getElementById("table").deleteRow(i+factor);
	factor--;
};

let approve = function(key, i)
{
	let rootRef = firebase.database().ref();
	let storesRef = rootRef.child('Facebook/' + key);
	storesRef.once('value').then(function(snapshot)
	{
		storesRef.set("1" + snapshot.val().substring(1,snapshot.val().length));
		let row = document.getElementById(i);
		let cell1 = row.children[0];
		let cell2 = row.children[1];
		let cell3 = row.children[2];
		let cell4 = row.children[3];
		while(row.firstChild)
			row.removeChild(row.firstChild);
		row.appendChild(cell1);
		let approved = "Approved";
		let cell_new = row.insertCell(1);
		cell_new.innerHTML = approved.fontcolor("green");
		row.appendChild(cell3);
		row.appendChild(cell4);
	});
};

let buttons_insert = function()
{
	let div = document.createElement("div");
	div.id = "top_buttons_div";
	div.className = "top_buttons_div";
	
    let link = document.createElement("a");
    //var txt= document.createTextNode("back to calendar");
	let backpic = document.createElement("IMG");
	backpic.src = "../buttons/back.png";
	backpic.height = 40;
	backpic.width = 40;
    link.appendChild(backpic);
    link.href = "../index.html";
    div.appendChild(link);
	
	
	let link2 = document.createElement("a");
	let dbpic = document.createElement("IMG");
	dbpic.src = "../buttons/facebook.png";
	dbpic.height = 40;
	dbpic.width = 40;
    link2.appendChild(dbpic);
    link2.href = "../admin_page/admin.html";
    div.appendChild(link2);
	
	let btn = document.createElement("button");
	btn.type = "button";
	btn.name = "log out";
	let log_out_pic = document.createElement("IMG");
	log_out_pic.src = "../buttons/log_out.png";
	log_out_pic.height = 40;
	log_out_pic.width = 40;
	btn.appendChild(log_out_pic);
	btn.addEventListener("click", function() { firebase.auth().signOut(); window.location = "../index.html"; });
	div.appendChild(btn);
	

	
	document.body.appendChild(div);
};
