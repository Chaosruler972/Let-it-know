$( document ).ready(function() 
{
    page_module.buttons_insert();
	page_module.createTable();
	page_module.db_rendering();
	page_module.addURL();
});


var page_module = (function () 
{
	let factor = 0; // factors deleting rows.. for rendering

	
	const createTable = function()
	{
		let table = document.createElement("table"); // creates the table
		table.id = "table";
		table.style.width = "100%";
		let div = document.createElement("div");
		div.id = "table_div";
		div.className = "table_div" ; 
		document.body.appendChild(div);
		div.appendChild(table);
		let row = table.insertRow(0); // first row with cells defination
		row.id = "row0";
		let cell1 = row.insertCell(0);
		let cell2 = row.insertCell(1);
		let cell3 = row.insertCell(2);
		let cell4 = row.insertCell(3);
		//let cell5 = row.insertCell(4);
		cell1.innerHTML = "Facebook page name or Google Calendar ID"; 
		cell2.innerHTML = "Status"
		cell3.innerHTML = "By";
		//cell4.innerHTML = "<input type='button' onclick='Add_new()' value='Add new Facebook page' style='width:100%'/>"; // add new Facebook page button
		//cell5.innerHTML = "<input type='button' onclick='add_google_calendar()' value='Add a new Google Calendar id' style='width:100%'/>"; // add new Google calendar button
		let add_facebook = document.createElement("input");
		add_facebook.type = 'button';
		add_facebook.onclick = Add_new;
		add_facebook.value = "Add new Facebook page";
		add_facebook.style.width = "50%";
		
		let add_calendar = document.createElement("input");
		add_calendar.type = 'button';
		add_calendar.onclick = add_google_calendar;
		add_calendar.value = "Add a new Google Calendar id";
		add_calendar.style.width = "50%";
		cell4.appendChild(add_facebook);
		cell4.appendChild(add_calendar);
		
	};
	
	const db_rendering = function()
	{
		let i = 1;
		let table = document.getElementById("table"); 
		let is_admin = 0;
		let admin_req = firebase.database();
		admin_req = admin_req.ref("admin");
		admin_req.once('value').then(function(snapshot) // checks to see if user is admin
		{
			if(firebase.auth().currentUser.email == snapshot.val() + "@gmail.com" )
			{
				is_admin=1; // marks it
				let manage_users_btn = document.createElement("button"); // adds admin only buttons
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
		let leadsRef = database.ref('Facebook'); // scans the facebook database
		leadsRef.once('value').then(function(snapshot) 
		{
			snapshot.forEach(function(childSnapshot) 
			{
				let email = childSnapshot.val().substring(1,childSnapshot.val().length); // grabs the matching username
				if(is_admin==1 || firebase.auth().currentUser.email == email + "@gmail.com" ) // checks if I am admin (can view all) or event is mine
				{
					let status = "";
					if(childSnapshot.val().charAt(0) == '0')
						status = "Pending"; // event status display
					else
						status = "Approved";
					let row = table.insertRow(i);
					row.id = i;
					let cell1 = row.insertCell(0);
					let cell2 = row.insertCell(1);
					if(status == "Approved")
						cell2.innerHTML = status.fontcolor("green"); // approved font color and text
					else
					{
						if(is_admin == 1)
						{
							let aprvbtn = document.createElement('input'); // admin can "approve" pages, so he gets an approve button
							aprvbtn.type = "button";
							aprvbtn.style.backgroundColor = "orange";
							aprvbtn.setAttribute("onClick","approve("+ '"' + childSnapshot.key+ '"'+"," + i +");"); // approve button linking
							aprvbtn.value = status;
							cell2.appendChild(aprvbtn);
						}
						else
						{
							cell2.innerHTML = status.fontcolor("orange");
						}
					}
					
					firebase.database().ref("Names/").once('value',function(snap) // grabs nickname
					{
						snap.forEach(function(shot)
						{
							if(shot.key == email)
							{
								let cell3 = document.createTextNode(shot.val());
								row.insertCell(2).appendChild(cell3);
								let cell4 = document.createElement('input');
								cell4.type = "button";
								//cell4.setAttribute("onClick","del("+ '"' + childSnapshot.key+ '"'+","+i +");"); // appends delete button
								cell4.onclick = function()
								{
									let rootRef = firebase.database().ref();
									let storesRef = rootRef.child('Facebook/' + childSnapshot.key); // grab the key
									storesRef.remove();
									table.deleteRow(row.rowIndex);
								}
								cell4.value = "Delete";
								row.insertCell(3).appendChild(cell4);
								cell1.innerHTML = "<a href='https://www.facebook.com/" +childSnapshot.key+ "'>" + childSnapshot.key + "</a>"; // facebook page name becomes a link
								i++;
							}
						});
					});
					
					
				}
			});
		});
		database.ref("Google/").once('value',function(snapshot)
		{
			snapshot.forEach(function(childSnapshot)
			{
				if(is_admin==1 || childSnapshot.id == firebase.auth().currentUser.email.substring(0,firebase.auth().currentUser.email.indexOf("@")))
				{
					let obj = childSnapshot.val();
					let row = table.insertRow(-1);
					let cell1 = row.insertCell(0);
					let cell2 = row.insertCell(1);
					let cell3 = row.insertCell(2);
					let cell4 = row.insertCell(3);
					cell1.innerHTML = "<a href='https://calendar.google.com/calendar/embed?src=" +obj.val+ "'>" + obj.val + "</a>"; // taken from aforementioned function
					let status;
					if(obj.approve == 1)
						status = "Approved";
					else
						status = "Pending";
					if(obj.approve == 0 && is_admin == 1)
					{
						let approve_btn = document.createElement('input'); // admin can "approve" pages, so he gets an approve button
						approve_btn.type = "button";
						approve_btn.style.backgroundColor = "orange";
						approve_btn.onclick = function()
						{
							obj.approve = 1;
							firebase.database().ref("Google/" + childSnapshot.key).set(obj);
							while (cell2.hasChildNodes())
							{
								cell2.removeChild(cell2.lastChild);
							}
							let txt2 = "Approved";
							cell2.innerHTML = txt2.fontcolor("green");

						};
						approve_btn.value = "Approve";
						cell2.appendChild(approve_btn);
					}
					else if(obj.approve == 0)
					{
						cell2.innerHTML = status.fontcolor("orange");
					}
					else
					{
						cell2.innerHTML = status.fontcolor("green");
					}
					let found = 0;
					firebase.database().ref("Names").once('value',function(snap)
					{
						snap.forEach(function(shot)
						{
							if(shot.key == obj.id)
							{
								cell3.innerHTML = shot.val();
								found=1;
							}
								
						});
					}).then(function()
					{
						if(found == 0)
							cell3.innerHTML = obj.id;
					});
					//cell3.innerHTML = obj.id;
					let del_but = document.createElement('input');
					del_but.type = "button";
					del_but.onclick = function()
					{
						firebase.database().ref("Google/" + childSnapshot.key).remove();
						table.deleteRow(row.rowIndex);
					};
					del_but.value = "Delete";
					cell4.appendChild(del_but);
				}
			});
		});
	};


	

	const Add_new = function() // add new facebook page function
	{
		let page = window.prompt("Enter a facebook page id (page id is viewable in format of https://www.facebook.com/{page-id} on any page)","");
		
		if(page == "" || page == null) // if name is illegal
		{
			
		}
		else
		{
			let rootRef = firebase.database().ref();
			let is_admin = 0;
			let admin_req = firebase.database();
			admin_req = admin_req.ref("admin");
			admin_req.once('value').then(function(snapshot) // if user is admin, marks it
			{
				if(firebase.auth().currentUser.email == snapshot.val() + "@gmail.com" )
				{
					is_admin = 1;
				}
				let autoapprove = 0;
				firebase.database().ref("Auto_approve").once('value', function(snapshot) // grabs auto approve status for user
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
						storesRef.set(autoapprove + firebase.auth().currentUser.email.substring(0,firebase.auth().currentUser.email.indexOf("@"))); // marks the username and auto approve status
						let row = document.getElementById("table").insertRow(document.getElementById("table").childElementCount); // updates the table
						row.id = document.getElementById("table").childElementCount;
						let cell1 = row.insertCell(0);
						let cell2 = row.insertCell(1);
						let cell3 = row.insertCell(2);
						cell1.innerHTML = "<a href='https://www.facebook.com/" +page+ "'>" + page + "</a>"; // taken from aforementioned function
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
						cell4.onclick = function()
						{
							let rootRef = firebase.database().ref();
							let storesRef = rootRef.child('Facebook/' + childSnapshot.key); // grab the key
							storesRef.remove();
							table.deleteRow(row.rowIndex);
						}
						cell4.value = "Delete";
						row.insertCell(3).appendChild(cell4);
				});
				
			});
		}

	};
	// https://calendar.google.com/calendar/embed?src=
	const add_google_calendar = function()
	{
		let page = window.prompt("Enter a google calendar ID","");
		if(page == "" || page == null) // if name is illegal
		{
			
		}
		else
		{
			let rootRef = firebase.database().ref();
			let is_admin = 0;
			let admin_req = firebase.database();
			admin_req = admin_req.ref("admin");
			admin_req.once('value').then(function(snapshot) // if user is admin, marks it
			{
				if(firebase.auth().currentUser.email == snapshot.val() + "@gmail.com" )
				{
					is_admin = 1;
				}
				let autoapprove = 0;
				firebase.database().ref("Auto_approve").once('value', function(snapshot) // grabs auto approve status for user
				{
					snapshot.forEach(function(ChildSnapshot)
					{
						
						if(ChildSnapshot.key+"@gmail.com" == firebase.auth().currentUser.email)
							autoapprove = ChildSnapshot.val();
					});
				}).then(function()
				{
					let child = rootRef.child('Google/');
					let obj = new Object();
					obj.val = page;
					obj.approve = autoapprove;
					obj.id = firebase.auth().currentUser.email.substring(0,firebase.auth().currentUser.email.indexOf("@"));
					let key = child.push(obj).key;
					let table = document.getElementById("table");
					let row = table.insertRow(-1);
					let cell1 = row.insertCell(0);
					let cell2 = row.insertCell(1);
					let cell3 = row.insertCell(2);
					let cell4 = row.insertCell(3);
					cell1.innerHTML = "<a href='https://calendar.google.com/calendar/embed?src=" +page+ "'>" + page + "</a>"; // taken from aforementioned function
					let status = "pending";
					if(is_admin == 1)
					{
						let approve_btn = document.createElement('input'); // admin can "approve" pages, so he gets an approve button
						approve_btn.type = "button";
						approve_btn.style.backgroundColor = "orange";
						//approve_btn.setAttribute("onClick","approve("+ '"' + childSnapshot.key+ '"'+"," + i +");"); // approve button linking
						approve_btn.onclick = function()
						{
							obj.approve = 1;
							firebase.database().ref("Google/" + key).set(obj);
							while (cell2.hasChildNodes())
							{
								cell2.removeChild(cell2.lastChild);
							}
							let txt2 = "Approved";
							cell2.innerHTML = txt2.fontcolor("green");

						};
						approve_btn.value = "Approve";
						cell2.appendChild(approve_btn);
					}
					else
					{
						cell2.innerHTML = status.fontcolor("orange");
					}
						let found = 0;
					firebase.database().ref("Names").once('value',function(snap)
					{
						snap.forEach(function(shot)
						{
							if(shot.key == obj.id)
							{
								cell3.innerHTML = shot.val();
								found=1;
							}
								
						});
					}).then(function()
					{
						if(found == 0)
							cell3.innerHTML = obj.id;
					});
					let del_but = document.createElement('input');
					del_but.type = "button";
					del_but.onclick = function()
					{
						firebase.database().ref("Google/" + key).remove();
						table.deleteRow(row.rowIndex);
					};
					del_but.value = "Delete";
					cell4.appendChild(del_but);
				});
			});
			
						
		}
	};	

	const approve = function(key, i) // approve facebook page, by admin
	{
		let rootRef = firebase.database().ref();
		let storesRef = rootRef.child('Facebook/' + key);
		storesRef.once('value').then(function(snapshot) // update the facebook database
		{
			storesRef.set("1" + snapshot.val().substring(1,snapshot.val().length)); // reappends database with correct data
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

	const buttons_insert = function() // navigation buttons
	{
		let div = document.createElement("div");
		div.id = "top_buttons_div";
		div.className = "top_buttons_div";
		
		let link = document.createElement("a"); // back to calendar button
		//var txt= document.createTextNode("back to calendar");
		let backpic = document.createElement("IMG");
		backpic.src = "../buttons/back.png";
		backpic.height = 40;
		backpic.width = 40;
		link.appendChild(backpic);
		link.href = "../index.html";
		div.appendChild(link);
		
		
		
		
		let btn = document.createElement("button"); // log out button
		btn.type = "button";
		btn.name = "log out";
		let log_out_pic = document.createElement("IMG");
		log_out_pic.src = "../buttons/log_out.png";
		log_out_pic.height = 40;
		log_out_pic.width = 40;
		btn.appendChild(log_out_pic);
		btn.addEventListener("click", function() { firebase.auth().signOut(); window.location = "../index.html"; }); // log out function
		
		div.appendChild(btn);
		
		let events_manage = document.createElement("a"); // manual event manage page
		let events_pic = document.createElement("IMG");
		events_pic.src = "../buttons/events_manage.png";
		events_pic.height = 40;
		events_pic.width = 40;
		events_manage.appendChild(events_pic);
		events_manage.href = "../events_manage_page/events.html"
		div.appendChild(events_manage);
		
		document.body.appendChild(div);
	};
	const addURL = function()
	{
		let div = document.createElement("div");
		div.id = "URL_DIV";
		div.className = "URL_DIV";
		
		let input = document.createElement("input");
		input.type = "input";
		
		let label = document.createElement("label");
		label.innerHTML = "Enter your website's URL here: ";
		label.appendChild(input);
		div.appendChild(label);
		
		let btn = document.createElement("button"); // log out button
		btn.type = "button";
		btn.onclick = function()
		{
			firebase.database().ref("Users/" + firebase.auth().currentUser.email.substring(0,firebase.auth().currentUser.email.indexOf("@")) + "/url").set(input.value);
			alert("Successfully set new URL");
		};
		btn.innerHTML  = "Submit new website";
		div.appendChild(btn);
		document.body.appendChild(div);
		
	};
	return {createTable,db_rendering,buttons_insert,addURL};

}());		
