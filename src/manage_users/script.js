$( document ).ready(function()
{
    page_module.buttons_insert(); // navigation buttons make
	page_module.createTable(); // creates the table that consists all the data
	page_module.db_rendering(); // fills the table with data

});
var page_module = (function ()
{
	let factor = 0; // when removing event, helping value
	let is_admin = 0; // small function that tells the script that the user might be admin
	let admin_email = ""; // who is the admin?
	let names = []; // nicknames array, for table

	const createTable = function()
	{
		let table = document.createElement("table");
		table.id = "table";
		let div = document.createElement("div");
		div.id = "table_div";
		div.className = "table_div";
		document.body.appendChild(div);
		div.appendChild(table);
		let row = table.insertRow(0); // first row consisting 6 cells, made to fine other rows
		let cell1 = row.insertCell(0);
		let cell2 = row.insertCell(1);
		let cell4 = row.insertCell(2);
		let empty = row.insertCell(3); // dummy cell for change color button
		let empty2 = row.insertCell(4); // dummy cell for delete button
		let cell5 = row.insertCell(5);
		let cell6 = row.insertCell(6);
		cell1.innerHTML = "Username";
		cell2.innerHTML = "Color sample"
		cell4.innerHTML = "Change color button";
		empty.innerHTML = "Password change";
		cell5.innerHTML = "<input type='button' onclick='page_module.Add_new()' value='Add new User'/>"; // calls the "add new" function button
		empty2.innerHTML = "Delete button";
		cell6.innerHTML = "Auto check";

	};

	const db_rendering = function() // divided into two parts function, made for filling table with user data
	{

		let table = document.getElementById("table");
		let admin_req = firebase.database();
		admin_req = admin_req.ref("admin");
		admin_req.once('value').then(function(snapshot) // verifying if the user is admin
		{
			if(firebase.auth().currentUser.email == snapshot.val() + "@gmail.com")
			{
				is_admin=1; // verified
				admin_email += snapshot.val(); // proven
			}
		}).then(function()
		{
			firebase.database().ref("Names").once('value',function(snapshot)
			{
				snapshot.forEach(function(childSnapshot) // creates a small array consisting of usernames and their matching nicknames
				{
					let obj = new Object();
					obj.id = childSnapshot.key;
					obj.name = childSnapshot.val();
					names.push(obj);
				});

			}).then(function()
			{
				firebase.database().ref("Auto_approve").once('value',function(snapshot) // appends to aforementioned array the data of user being "auto approven"
				{
					snapshot.forEach(function(childSnapshot)
					{
						names.forEach(function(val)
						{
							if(val.id == childSnapshot.key)
							{
								if(childSnapshot.val() == 1)
									val.auto = true;
								else
									val.auto = false;
							}
						});
					});

				});
			}).then(continue_rendering); // second part of the rendering function
		});
	};
	const continue_rendering = function()
	{
		if(is_admin != 1)
			return;
		let i = 1;
		let database = firebase.database();
		let leadsRef = database.ref('Users');
		leadsRef.once('value').then(function(snapshot) // for each user on our database
		{
			snapshot.forEach(function(childSnapshot)
			{
				if(is_admin==1)
				{
				//	let color = childSnapshot.val().substring(childSnapshot.val().indexOf("@",childSnapshot.val().indexOf("@")+1)+1,childSnapshot.val().length);
				//	let email = childSnapshot.val().substring(0,childSnapshot.val().indexOf(color)-1);
					let color = childSnapshot.val().color; // grab the matching color
					let url = childSnapshot.val().url;
					let email = childSnapshot.key; // username
					let row = table.insertRow(i); // add a row for him
					row.id = i;
					let cell1 = row.insertCell(0);
					names.forEach(function(val,index,arr) // scans if that user is actually the admin (the user we are rendering now)
					{
						if(val.id == email) // adds buttons that are visible to admin only
						{
							let change_name_input = document.createElement('INPUT');
							change_name_input.id = val.id;
							change_name_input.value = val.name;
							let change_name_btn = document.createElement('button');
							change_name_btn.type = "button";
							change_name_btn.innerHTML = "Change the name";
							change_name_btn.addEventListener('click',function()
							{
								if(change_name_input.value != null && change_name_input.value.length > 0) // verifies new data is possible
								{
									firebase.database().ref("Names/" + email).set(change_name_input.value); // inputs it
								}
							});
							cell1.appendChild(change_name_input);
							cell1.appendChild(change_name_btn);
						}
					});
					//cell1.innerHTML = email;
					let cell2 = row.insertCell(1);
					cell2.style.backgroundColor = "#"+color; // marks the "color sample" cell
					cell2.name = color;
					cell2.id = "a"+i;
					cell2.className = "a"+i;
					cell2.value = color;
					let change_color = document.createElement('input');
					change_color.type = "image";
					change_color.setAttribute("onClick","changeColor(" + '"' + childSnapshot.key + '",' + i+ ");"); // links to change color function
					change_color.src = "../buttons/color_picker.png";
					change_color.height = 40; // predefined size
					change_color.width = 40; // predefined size
					//cell2.appendChild(change_color);
					let cell2_extra = row.insertCell(2); // adds the actual change color functionality
					cell2_extra.appendChild(change_color);
					let cell3_input = document.createElement('input');
					cell3_input.type = "button";
					cell3_input.setAttribute("onClick","change_password("+ '"' + childSnapshot.key+ '",' + i +"," + "'" + email + "'" + ");"); // links the change password function
					cell3_input.value = "Change password";
					//row.appendChild(cell3);
					let cell3 = row.insertCell(3);
					cell3.appendChild(cell3_input);
					let cell4_input = document.createElement('input');
					cell4_input.type = "button";
					//cell4_input.setAttribute("onClick","del("+ '"' + childSnapshot.key+ '",' + i +"," + "'" + email + "'" + ");"); // links the delete function
					cell4_input.onclick = function()
					{
						del(childSnapshot.key,i,email);
						table.deleteRow(row.rowIndex);
					}
					cell4_input.value = "Delete";
					if(email == admin_email) // if user that we are rendering is the actual admin
						cell4_input.disabled = true; // can't be deleted
					//row.appendChild(cell4);
					let cell4 = row.insertCell(4);
					cell4.appendChild(cell4_input);
          /*
					let make_admin = document.createElement('button');
					make_admin.type = "button";
					make_admin.innerHTML = "make admin"; // defracted, button to make admin
					make_admin.addEventListener('click',function()
					{
						firebase.database().ref("admin").set(email); // set new admin data
						alert("Successfully moved admin rights to " + email + " Returning to main screen");
						window.location = "../index.html";
					});
					let cell5 = row.insertCell(5);
					cell5.appendChild(make_admin);
          */
					row.insertCell(5).innerHTML = " "; // dummy cell
					let cell6 = row.insertCell(6);
          /*
					if(email == admin_email)
					{
						make_admin.disabled = true; // if user is already admin,he can't be made into admin lol
					}
          */
					let checkbox = document.createElement("INPUT");
					checkbox.setAttribute("type", "checkbox");
					names.forEach(function(val) // auto approve checkbox
					{
						if(val.id == email) // if user is already on auto approve, mark as true
						{
							checkbox.checked = val.auto;
						}
					});
					checkbox.addEventListener('click', function()
					{
						if(checkbox.checked == true)
							firebase.database().ref("Auto_approve/" + email).set(1);
						else
							firebase.database().ref("Auto_approve/" + email).set(0);
					});
					cell6.appendChild(checkbox);
					i++;
					
					const changeColor = function(key, index)
					{
						let row = document.getElementById(index); // backs up previous row data
						let cell1 = row.children[0];
						let cell2 = row.children[1];
						let cell3 = row.children[2];
						let cell4 = row.children[3];
						let cell5 = row.children[4];
						let cell6 = row.children[5];
						let cell7 = row.children[6];
						while(row.firstChild)
							row.removeChild(row.firstChild);
						row.appendChild(cell1);
						row.appendChild(cell2);
						var input = document.createElement("INPUT"); // adds the UI for choosing a new color while appends the button out
						var picker = new jscolor(input);
						picker.valueElement = "";
						picker.fromString("#" + cell2.name);
						input.value = "Click";
						input.size = 2;
						var btn = document.createElement("button");
						btn.type = "button";
						btn.innerHTML = "Color Submit";
						btn.addEventListener("click", function()
						{
							let colors = input.style.backgroundColor.split(',');
							let r = parseInt(colors[0].substring(4)).toString(16) + "";
							let g = parseInt(colors[1]).toString(16)+ "";
							let b = parseInt(colors[2]).toString(16)+ ""; // grabs the color data
							if(r.length == 1) // refactors value to be two digit ALWAYS
								r = "0" + r;
							if(g.length == 1)
								g = "0" + g;
							if(b.length == 1)
								b = "0" + b;
							let color_string = r+""+g+""+b;
							cell2.name = color_string;
							firebase.database().ref('Users/'+key).set({color: color_string, url: url}); // updates database
							while(row.firstChild) // removes all the cells again
								row.removeChild(row.firstChild);
							row.append(cell1); // inserts backup
							row.append(cell2);
							row.append(cell3);
							row.append(cell4);
							row.append(cell5);
							row.append(cell6);
							row.append(cell7);
							cell2.style.backgroundColor = "#" + color_string; // recolors
						});
						row.appendChild(input);
						row.appendChild(btn);
						row.appendChild(cell4);
						row.appendChild(cell5);
						row.appendChild(cell6);
						row.appendChild(cell7); // appends the cells that has nothing to do with the request
					};
				}
			});
		});
	};



	const Add_new = function()
	{
		window.location = "../create_user/create.html"; // add new user page
	};

	const del = function(key,index,email)
	{
		let rootRef = firebase.database().ref();
		let storesRef = rootRef.child('Users/' + key);
		//storesRef.remove();
		let cred = "";
		let backup = firebase.auth().currentUser; // backs up admin creditnitals
		if(is_admin == 1)
		{
			let admin_email = "";
			let admin_pw = "";
			firebase.database().ref().child("Crds").once('value').then(function(snapshot)  // search for target user username and pw
			{
				snapshot.forEach(function(childSnapshot)
				{
					if(email == childSnapshot.key)
					{
						cred += childSnapshot.val();
					}
					if(childSnapshot.key + "@gmail.com"  == firebase.auth().currentUser.email) // and saves admin username and pw
					{
						admin_email += firebase.auth().currentUser.email;
						admin_pw += childSnapshot.val();
					}
				});
			}).then(function()
			{
				if(cred != "")
				{
					firebase.auth().signInWithEmailAndPassword(email + "@gmail.com" , cred).catch(function(error)  // sign in as target user
					{
						console.log(error.message);
					}).then(function()
					{
						if(firebase.auth().currentUser.uid != backup.uid && firebase.auth().currentUser!=null)
						{
							firebase.auth().currentUser.delete().then(function() {  // requests server to be deleted
								firebase.auth().signInWithEmailAndPassword(admin_email, admin_pw).catch(function(error) // sign in as admin
								{
									console.log(error.message);
								}).then(function()
								{
										storesRef.remove(); // removes user data from database on matching color
										firebase.database().ref().child("Crds/" + email).remove(); // removes user data creditnitals
										firebase.database().ref().child("Facebook").once('value',function(snapshot) // removes all matching facebook page of user
										{
											snapshot.forEach(function(childSnapshot)
											{
												if(email == childSnapshot.val().substring(1,childSnapshot.val().length))
													firebase.database().ref().child("Facebook/" + childSnapshot.key).remove();
											});

										});
										firebase.database().ref().child("Users/" + email).remove(); // removes color match
										firebase.database().ref().child("Names/" + email).remove(); // nickname match
										firebase.database().ref().child("Auto_approve/" + email).remove(); // auto approve data
                    firebase.database().ref().child("Events/").once('value',function(snapshot) // removes all users "manual events"
                    {
                      snapshot.forEach(function(childSnapshot)
                      {
                        if(childSnapshot.val().id == email)
                          firebase.database().ref().child("Events/" + childSnapshot.key).remove();
                      });
                    });
					firebase.database().ref().child("Google/").once('value',function(snapshot) // removes all users Google calenders
                    {
                      snapshot.forEach(function(childSnapshot)
                      {
                        if(childSnapshot.val().id == email)
                          firebase.database().ref().child("Google/" + childSnapshot.key).remove();
                      });
                    });
					
										//document.getElementById("table").deleteRow(index+factor); // deletes user from table (visual)
										//factor--;
								});

							}, function(error) {
							  console.log(error);
							});
						}
					});
				}
			});
		}

	};

	const change_password = function(key,index,email)
	{
		let row = document.getElementById(index); // backs up all the previous "cells"
		let cell1 = row.children[0];
		let cell2 = row.children[1];
		let cell3 = row.children[2];
		let cell4 = row.children[3];
		let cell5 = row.children[4];
		let cell6 = row.children[5];
		let cell7 = row.children[6];
		while(row.firstChild) // removes all cells from line
			row.removeChild(row.firstChild);
		row.appendChild(cell1);
		row.appendChild(cell2);
		var input = document.createElement("INPUT"); // adds a new cell to replace the password change button, cell will let you type the new password
		input.id = key+index;
		var btn = document.createElement("button");
		btn.type = "button";
		btn.innerHTML = "Password Submit";
		btn.addEventListener("click", function()
		{
			let pw = document.getElementById(key+index).value;
			if(change_pw(key,index,email,pw)==true) // if the password was acccpeted
			{
				while(row.firstChild) // reappend all backed up cells (removes the old ones first)
					row.removeChild(row.firstChild);
				row.append(cell1);
				row.append(cell2);
				row.append(cell3);
				row.append(cell4);
				row.append(cell5);
				row.append(cell6);
				row.append(cell7);
				alert("Succeeded");
			}
			else
				alert("password's length must be above 6, all characters must be from english alphabet including numbers and special characters");
		});
		row.appendChild(cell2);
		row.appendChild(input);
		row.appendChild(btn);
		row.appendChild(cell5);
		row.appendChild(cell6);
		row.appendChild(cell7); // re appends the extra cells that has nothing to do with the request
	};

	const change_pw = function(key,index,email,pw) // actually tries to change pw
	{
		if(pw.length < 6)
		{
			alert("Not enough characters, please type 6 or more");
			return false;
		}
		let rootRef = firebase.database().ref();
		let storesRef = rootRef.child('Users/' + key);
		//storesRef.remove();
		let cred = "";
		let backup = firebase.auth().currentUser;
		if(is_admin == 1)
		{
			let admin_email = "";
			let admin_pw = "";
			firebase.database().ref().child("Crds").once('value').then(function(snapshot)  // scans the entire db
			{
				snapshot.forEach(function(childSnapshot)
				{
					if(email == childSnapshot.key) // grabs username creditnitals
					{
						cred += childSnapshot.val();
					}
					if(childSnapshot.key + "@gmail.com"  == firebase.auth().currentUser.email) // grabs admin creditnitals
					{
						admin_email += firebase.auth().currentUser.email;
						admin_pw += childSnapshot.val();
					}
				});
			}).then(function()
			{
				if(cred != "")
				{
					firebase.auth().signInWithEmailAndPassword(email + "@gmail.com" , cred).catch(function(error)  // sign in as target user
					{
						console.log(error.message);
					}).then(function()
					{
						if(firebase.auth().currentUser.uid != backup.uid && firebase.auth().currentUser!=null)
						{
							firebase.auth().currentUser.updatePassword(pw).then(function() { // updates the password on auth
								firebase.auth().signInWithEmailAndPassword(admin_email, admin_pw).catch(function(error)  // logs in with admin
								{
									console.log(error.message);
								}).then(function()
								{
										firebase.database().ref().child("Crds/" + email).set(pw); // updates creditnitals
								});

							}, function(error) {
							  console.log(error);
							});
						}
					});
				}
			});
		}
		return true;
	};


	const buttons_insert = function()
	{
			let div = document.createElement("div"); // div to hold navigation buttons
			div.id = "top_buttons_div";
			div.className = "top_buttons_div";
			document.body.appendChild(div);


			let link = document.createElement("a"); // "back to calendar" button
			//var txt= document.createTextNode("back to calendar");
			let backpic = document.createElement("IMG");
			backpic.src = "../buttons/back.png";
			backpic.height = 40;
			backpic.width = 40;
			link.appendChild(backpic);
			link.href = "../index.html";
			div.appendChild(link);

			let link2 = document.createElement("a"); // "event rendering from databse of facebook/google" page button
			let dbpic = document.createElement("IMG");
			dbpic.src = "../buttons/facebook.png";
			dbpic.height = 40;
			dbpic.width = 40;
			link2.appendChild(dbpic);
			link2.href = "../admin_page/admin.html";
			div.appendChild(link2);

			let btn = document.createElement("button"); // log out button
			btn.type = "button";
			btn.name = "log out";
			let log_out_pic = document.createElement("IMG");
			log_out_pic.src = "../buttons/log_out.png";
			log_out_pic.height = 40;
			log_out_pic.width = 40;
			btn.appendChild(log_out_pic);
			btn.addEventListener("click", function() { firebase.auth().signOut(); window.location = "../index.html"; }); // sign out command
			div.appendChild(btn);

	};
		return {buttons_insert,createTable,db_rendering,Add_new,};
}());
