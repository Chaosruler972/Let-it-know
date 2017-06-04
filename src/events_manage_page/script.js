let users = [];
$( document ).ready(function() 
{
    buttons_insert();
	createTable();
	firebase.database().ref("Names").once('value',function(snapshot)
	{
		snapshot.forEach(function(childSnapshot)
		{
			let obj = new Object();
			obj.id = childSnapshot.key;
			obj.nick = childSnapshot.val();
			users.push(obj);
		});
	}).then(db_rendering);
	//db_rendering();
	
});

Date.prototype.toDateInputValue = (function() {
    var local = new Date(this);
    local.setMinutes(this.getMinutes() - this.getTimezoneOffset());
    return local.toJSON().slice(0,10);
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
	let leadsRef = database.ref('Events');
	
	leadsRef.once('value').then(function(snapshot) 
	{
		snapshot.forEach(function(childSnapshot) 
		{
			if(is_admin == 1 || childSnapshot.val().id == firebase.auth().currentUser.email.substring(0,firebase.auth().currentUser.email.indexOf("@")))
			{	
				let row = table.insertRow(i);
				let cell1 = row.insertCell(0);
				let cell2 = row.insertCell(1);
				let cell3 = row.insertCell(2);
				let cell4 = row.insertCell(3);
				let cell5 = row.insertCell(4);
				let cell6 = row.insertCell(5);
				let cell7 = row.insertCell(6);
				let cell8 = row.insertCell(7);
				let cell9 = row.insertCell(8);
				let cell10 = row.insertCell(9);
				
				cell1.innerHTML = childSnapshot.val().name;
				
				if(childSnapshot.val().approve == 1)
				{
					let txt = "Approved";
					cell2.innerHTML = txt.fontcolor('green');
				}
				else
				{
					let txt = "Pending";
					cell2.innerHTML = txt.fontcolor('orange');
				}
				users.forEach(function(val)
				{
					if(val.id == childSnapshot.val().id)
					{
						cell3.innerHTML = val.nick;
					}
					
				});
				let s = new Date(childSnapshot.val().start_time);
				let e = new Date(childSnapshot.val().end_time);
				let start_date = document.createElement("input");
				start_date.required = "true";
				start_date.setAttribute("type","date");
				let date_str_1 = s.getFullYear() + "-";
				if(s.getMonth() >= 9)
				{
					let month = 1 + s.getMonth();
					date_str_1 += month + "-";
				}
				else
				{
					let month = 1 + s.getMonth();
					date_str_1 += "0" + month + "-";
				}
				if(s.getDate() >= 9)
				{
					let date = s.getDate() + 1
					date_str_1 += date;
				}
				else
				{
					let date = s.getDate() + 1
					date_str_1 += "0" + date;
				}
				start_date.value = date_str_1;
				cell4.appendChild(start_date);
				
				let start_time = document.createElement("input");
				start_time.required = "true";
				start_time.setAttribute("type","time");
				let time_str_1 = "";
				if(s.getHours() < 10)
					time_str_1 += "0" + s.getHours()+ ":";
				else
					time_str_1 += s.getHours()+ ":";
				if(s.getMinutes() < 10)
					time_str_1 += "0" + s.getMinutes();
				else
					time_str_1 += s.getMinutes();
				start_time.value = time_str_1;
				cell5.appendChild(start_time);
				
				
				
				let end_date = document.createElement("input");
				end_date.required = "true";
				end_date.setAttribute("type","date");
				date_str_1 = s.getFullYear() + "-";
				if(e.getMonth() >= 9)
				{
					let month = 1 + e.getMonth();
					date_str_1 += month + "-";
				}
				else
				{
					let month = 1 + e.getMonth();
					date_str_1 += "0" + month + "-";
				}
				if(e.getDate() >= 9)
				{
					let date = e.getDate() + 1
					date_str_1 += date;
				}
				else
				{
					let date = e.getDate() + 1
					date_str_1 += "0" + date;
				}
				end_date.value = date_str_1;
				cell6.appendChild(end_date);
				
				let end_time = document.createElement("input");
				end_time.required = "true";
				end_time.setAttribute("type","time");
				time_str_1 = "";
				if(e.getHours() < 10)
					time_str_1 += "0" + e.getHours()+ ":";
				else
					time_str_1 += e.getHours()+ ":";
				if(e.getMinutes() < 10)
					time_str_1 += "0" + e.getMinutes();
				else
					time_str_1 += e.getMinutes();
				end_time.value = time_str_1;
				cell7.appendChild(end_time);
				
				
				let url = document.createElement("INPUT");
				url.setAttribute("type", "input");
				url.value = childSnapshot.val().url;
				cell8.appendChild(url); 
				
				
				let update_btn = document.createElement("button");
				update_btn.type = "button";
				update_btn.innerHTML = "Update";
				update_btn.addEventListener("click",function() 
				{
					let start_date_val = start_date.value;
					let end_date_val = end_date.value;
					let start_time_val = start_time.value;
					let end_time_val = end_time.value
					let events_name_val = cell1.innerHTML;
					let url_val = url.value;	
					if(start_date_val.length == 0 || end_date_val.length == 0 || start_time_val.length == 0 || end_time_val.length == 0 || events_name_val.length == 0)
						return false;
					let year_start = start_date_val.substring(0,start_date_val.indexOf("-"));
					let second_part1 = start_date_val.substring(start_date_val.indexOf("-")+1,start_date_val.length);
					let month_start = second_part1.substring(0,second_part1.indexOf("-"));
					let day_start = second_part1.substring(second_part1.indexOf("-")+1,second_part1.length);
					let hour_start = start_time_val.substring(0,start_time_val.indexOf(":"));
					let min_start = start_time_val.substring(start_time_val.indexOf(":")+1,start_time_val.length);
					
					let year_end = end_date_val.substring(0,end_date_val.indexOf("-"));
					let second_part2 = end_date_val.substring(end_date_val.indexOf("-")+1,end_date_val.length);
					let month_end = second_part2.substring(0,second_part2.indexOf("-"));
					let day_end = second_part2.substring(second_part2.indexOf("-")+1,second_part1.length);
					let hour_end = end_time_val.substring(0,end_time_val.indexOf(":"));
					let min_end = end_time_val.substring(end_time_val.indexOf(":")+1,end_time_val.length);
					
					let s = new Date(year_start,month_start,day_start,hour_start,min_start,0,0);
					let e = new Date(year_end,month_end,day_end,hour_end,min_end,0,0);
					s.setMonth(month_start-1);
					e.setMonth(month_end-1);
					
					if(s.valueOf() - e.valueOf() > 0)
					{
						
						alert("Start time can't be after end time");
						return false;
					}
					
					let obj = new Object();
					obj.start_time = s.valueOf();
					obj.end_time = e.valueOf();
					obj.name = events_name_val;
					obj.url = url_val;
					obj.id = childSnapshot.val().id;
					obj.approve = childSnapshot.val().approve;
					firebase.database().ref("Events/" + childSnapshot.key).set(obj);
				
				
					alert("successfull");
					return false;
				});
				cell9.appendChild(update_btn);
				let delete_button = document.createElement("button");
				delete_button.type = "button";
				delete_button.innerHTML = "Delete";
				delete_button.addEventListener("click",function() 
				{
					firebase.database().ref("Events/" + childSnapshot.key).remove();
					table.deleteRow(i-1);
				});
				cell10.appendChild(delete_button);
				
				i++;
			}
		});
	});
	
	
	const add_new_event_ui = function()
	{
		
		
	
		let div = document.getElementById("new_date_form");
		
		let form_obj = document.createElement("FORM");
		form_obj.id = "form";
		form_obj.autocomplete = "false"; // turns on auto complete on form
		form_obj.acceptCharset = "UTF-8"; // utilizes english-only with special characters incl @
		
		div.appendChild(form_obj);
		
		let event_name = document.createElement("INPUT");
		event_name.setAttribute("type", "input");
		event_name.required = "true";
		event_name.autocomplete = "false";
		event_name.form = form_obj;
		event_name.id = "name";
		event_name.className = "name";
		
		div.appendChild(document.createTextNode("Event's name: "));
		div.appendChild(event_name);
		
		div.appendChild(document.createElement("br"));
		users.forEach(function(val)
		{
			if(val.id == firebase.auth().currentUser.email.substring(0,firebase.auth().currentUser.email.indexOf("@")))
			{
				let txt = document.createTextNode("By :" + val.nick);
				div.appendChild(txt);
			}
		});
		
		div.appendChild(document.createElement("br"));
		
		div.appendChild(document.createTextNode("Start date: "));
		let start_date = document.createElement("input");
		start_date.required = "true";
		start_date.setAttribute("type","date");
		start_date.id = "start_date";
		
		div.appendChild(start_date);
		
		div.appendChild(document.createElement("br"));
		div.appendChild(document.createTextNode("Start time: "));
		let start = document.createElement("INPUT");
		start.setAttribute("type", "time");
		start.id = "start";
		start.required = "true";
		start.form = form_obj;
		div.appendChild(start);
		
		
		
		div.appendChild(document.createElement("br"));
		div.appendChild(document.createTextNode("End date: "));
		let end_date = document.createElement("input");
		end_date.required = "true";
		end_date.setAttribute("type","date");
		end_date.id = "end_date";
		
		div.appendChild(end_date);
		
		div.appendChild(document.createElement("br"));
		div.appendChild(document.createTextNode("End time: "));
		let end = document.createElement("INPUT");
		end.setAttribute("type", "time");
		end.id = "end";
		end.required = "true";
		end.form = form_obj;
		div.appendChild(end);

		div.appendChild(document.createElement("br"));
		
		let url = document.createElement("INPUT");
		url.setAttribute("type", "input");
		url.required = "true";
		url.autocomplete = "false";
		url.form = form_obj;
		url.id = "url";
		url.className = "url";
		div.appendChild(document.createTextNode("URL (optional): "));
		div.appendChild(url);
		
		const add_event = function()
		{
			let start_date_val = start_date.value;
			let end_date_val = end_date.value;
			let start_time_val = start.value;
			let end_time_val = end.value
			let events_name_val = event_name.value;
			let url_val = url.value;	
			if(start_date_val.length == 0 || end_date_val.length == 0 || start_time_val.length == 0 || end_time_val.length == 0 || events_name_val.length == 0)
				return false;
			let year_start = start_date_val.substring(0,start_date_val.indexOf("-"));
			let second_part1 = start_date_val.substring(start_date_val.indexOf("-")+1,start_date_val.length);
			let month_start = second_part1.substring(0,second_part1.indexOf("-"));
			let day_start = second_part1.substring(second_part1.indexOf("-")+1,second_part1.length);
			let hour_start = start_time_val.substring(0,start_time_val.indexOf(":"));
			let min_start = start_time_val.substring(start_time_val.indexOf(":")+1,start_time_val.length);
			
			let year_end = end_date_val.substring(0,end_date_val.indexOf("-"));
			let second_part2 = end_date_val.substring(end_date_val.indexOf("-")+1,end_date_val.length);
			let month_end = second_part2.substring(0,second_part2.indexOf("-"));
			let day_end = second_part2.substring(second_part2.indexOf("-")+1,second_part1.length);
			let hour_end = end_time_val.substring(0,end_time_val.indexOf(":"));
			let min_end = end_time_val.substring(end_time_val.indexOf(":")+1,end_time_val.length);
			
			let s = new Date(year_start,month_start,day_start,hour_start,min_start,0,0);
			let e = new Date(year_end,month_end,day_end,hour_end,min_end,0,0);
			s.setMonth(month_start-1);
			e.setMonth(month_end-1);
			
			if(s.valueOf() - e.valueOf() > 0)
			{
				
				alert("Start time can't be after end time");
				return false;
			}
			
			let obj = new Object();
			obj.start_time = s.valueOf();
			obj.end_time = e.valueOf();
			obj.name = events_name_val;
			obj.url = url_val;
			obj.id = firebase.auth().currentUser.email.substring(0,firebase.auth().currentUser.email.indexOf("@"));
			firebase.database().ref("Auto_approve").once('value',function(snapshot)
			{
				snapshot.forEach(function(childSnapshot)
				{
					if(childSnapshot.key == firebase.auth().currentUser.email.substring(0,firebase.auth().currentUser.email.indexOf("@")))
					{
						obj.approve = childSnapshot.val();
						firebase.database().ref("Events").push(obj);
					}
				});
			});
		
		
			alert("successfull, refreshing");
			location.reload();
			return false;
		};
		
		div.appendChild(document.createElement("br"));
		let btn = document.createElement("button");
		btn.form = form_obj;
		btn.formNoValidate = false; // validate data
		btn.type = "submit";
		btn.name = "submit";
		btn.className = "Submit_button";
		btn.innerHTML = "Add new";
		btn.onclick = add_event;
		div.appendChild(btn);

	
	};
	
	add_new_event_ui();
};



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
	let cell5 = row.insertCell(4);
	let cell6 = row.insertCell(5);
	let cell7 = row.insertCell(6);
	let cell8 = row.insertCell(7);
	let cell9 = row.insertCell(8);
	let cell10 = row.insertCell(9);
	cell1.innerHTML = "Event name";
	cell2.innerHTML = "Status";
	cell3.innerHTML = "By";
	cell4.innerHTML = "Start Date";
	cell5.innerHTML = "Start time";
	cell6.innerHTML = "End Date";
	cell7.innerHTML = "End time";
	cell8.innerHTML = "URL";
	cell9.innerHTML = "update button";
	cell10.innerHTML = "Delete button";
	
	let formdiv = document.createElement("div");
	formdiv.className = "new_date_form";
	formdiv.id = "new_date_form";
	document.body.appendChild(formdiv);
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
