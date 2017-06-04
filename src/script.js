var page_module = (function () 
{
	let facebook_app_id = "1888264188125920";
	let pw = "Mgo6B97nHZKdwXMiGw5P";
	let events = new Array();
	let users = new Array();
	let fb = 0;
	let initModule = function()
	{
		//// Calendar initation virtual space ////
		get_users_db();
		let elemDiv = document.createElement('div');
		elemDiv.id = "calendar1"
		elemDiv.className = "calendar1";
		document.body.appendChild(elemDiv);
		  $('#calendar1').fullCalendar(
		{
		header: {
		  left: 'prev,next,today',
		  center: 'title',
		  right: 'month,agendaWeek,agendaDay'
		  },
		  theme: true,
		  eventRender: function(event, element) 
		  {
			element.attr('title', event.tooltip);
		  },
		 eventClick: function(event)
		 {
			  if (event.url) 
			  {
				window.open(event.url);
				return false;
			  }
		 }	 
		});
	  //// END OF Calendar initation virtual space ////
	  
	  
	  /// Facebook API initation virtual space ///
	  // depends on IP being either localhost or specific firebase-project approved by author
		FB.init(
		{
		  appId      : ''+facebook_app_id,
		  xfbml      : true,
		  version    : 'v2.8'
		}); // API call function to "throw" API ID and be approved upon IP filtering of Facebook management
		/// END of Facebook API initation virtual space ///
		
	};
	
	let gather_all_events_from_all_facebook_pages = function()
	{
		// subdibision into local function!
		
		let add_event_to_calendar = function(id, name, date, email, enddate) // subfunction responisble for adding event to fullcalendar API event and render that
		{
			let color = "#";
			users.forEach(function(val)
			{
				if(val.id == email)
					color+=val.color;
				return;
			});
			let event;
			if(enddate == undefined)
				event = {id: email , title: name , start: date , url: 'https://www.facebook.com/events/'+id , backgroundColor: color, event_source: email, tooltip: name};
			else
				event = {id: email , title: name , start: date, end:enddate , url: 'https://www.facebook.com/events/'+id , backgroundColor: color, event_source: email, tooltip: name};
			
			events.push(event);
		};
		
		let withdraw_events_from_facebook_page = function(data, email) // subfunction responsible for scanning entire event database file from facebook page
		{
			 // subfunction to print results into Fullcalendar API
			let i;
			for(i=0; i<data.length; i++)
			{
				let event_date = new Date(); // sets a new Date for event date formating to be dynamic
				event_date.setISO8601(data[i].start_time); // converts time from Facebook API date format to Javascript Date format
				add_event_to_calendar(data[i].id, data[i].name, event_date, email);		 
			}
		};
		
		let page_through_all_events_from_response = function(response,email)
		{
			withdraw_events_from_facebook_page(response.data,email);
			 if(response.paging.next)
			 {
				$.getJSON(response.paging.next, function(next_data)
				{                
					page_through_all_events_from_response(next_data,email);
				});
			} 
		};
		
		let add_events_from_page = function(str, email) // subfunction responsible for doing the API request for the event database
		{
			FB.api
			(
				"/"+ str+"/events?access_token="+config_module.getaccessToken(pw), // the HTTP request defined in FB API
				function (response)  
				{
				  if (response && !response.error) // if request was approved by server 
				  {
						page_through_all_events_from_response(response, email);
				  }
				  else
				  {
					  console.log(response);
				  }
				}
			);
				
		};
		let scan_for_direct_events = function()
		{
			let database = firebase.database();
			let leadsRef = database.ref('Events');
			leadsRef.once('value',function(snapshot) 
			{
				snapshot.forEach(function(childSnapshot) 
				{
					if(childSnapshot.val().approve == 1)
					{
						add_event_to_calendar(childSnapshot.val().id, childSnapshot.val().name, new Date(childSnapshot.val().start_time), childSnapshot.val().id, new Date(childSnapshot.val().end_time));
					}
				});
				
			});
		};
		let scan_all_db_for_pages = function() // subfunction responsible for facebook DB load via Firebase API call
		{
			let database = firebase.database();
			let leadsRef = database.ref('Facebook');
			leadsRef.once('value',function(snapshot) 
			{
				snapshot.forEach(function(childSnapshot) 
				{
					if(childSnapshot.val().charAt(0) == '1')
						add_events_from_page(childSnapshot.key,childSnapshot.val().substring(1, childSnapshot.val().length));
				});
				
			});
		};
		scan_for_direct_events();
		scan_all_db_for_pages();
		$(document).ajaxStop(function () 
		{
			local_storage_func();
			  // 0 === $.active
		});
	};
	let create_buttons = function()
	{
		
		let div = document.createElement("div");
		div.id = "top_buttons_div";
		div.className = "top_buttons_div";
		let back_button_build = function()
		{
			let link = document.createElement("a");
			let pic = document.createElement("IMG");
			pic.src = "buttons/admin.png";
			pic.height = 40;
			pic.width = 40;
			link.appendChild(pic);
			link.href = "login_page/login.html";
			div.appendChild(link);
		};
		document.body.appendChild(div);
		back_button_build();
	};
	let get_users_db = function()
	{
		let database = firebase.database();
		let leadsRef = database.ref('Users');
		leadsRef.once('value',function(snapshot) 
		{
			snapshot.forEach(function(childSnapshot) 
			{
				let email = childSnapshot.key;
				let color = childSnapshot.val();
				let user_obj = new Object();
				user_obj.id = email;
				user_obj.key = childSnapshot.key;
				user_obj.color = color;
				users.push(user_obj);
			});
			
		}).then(function()
		{
			database.ref("Names").once('value',function(snapshot2)
			{
				snapshot2.forEach(function(childSnapshot)
				{
					users.forEach(function(val,index,arr)
					{
						if(val.id == childSnapshot.key)
							val.name = childSnapshot.val();
					});
				});
			}).then(build_filter_table)
		});
	};
	let build_filter_table = function()
	{
		let checkbox_handler = function(e)
		{
			let status = "";
			if(e.srcElement.checked == true)
			{
				status="addEventSource";
				localStorage.setItem(e.srcElement.id,"1");
			}
			else
			{
				status = "removeEvents";
				localStorage.setItem(e.srcElement.id,"0");
			}
			add_remove_by_id(e.srcElement.id,status);
		};
		
		let i=1;
		let table = document.createElement("table");
		table.id = "table";
		let main_row = table.insertRow(0);
		let main_cell1 = main_row.insertCell(0);
		main_cell1.innerHTML = "E-mail/Name";
		let main_cell2 = main_row.insertCell(1);
		main_cell2.innerHTML = "Color";
		users.forEach(function(val,index,arr)
		{
			let row = table.insertRow(i);
			let cell1 = row.insertCell(0);
			if(val.name == null)
				cell1.innerHTML = val.id;
			else
				cell1.innerHTML = val.name;
			let cell2 = row.insertCell(1);
			cell2.style.backgroundColor = "#" + val.color;
			let cell3 = row.insertCell(2);
			let checkbox = document.createElement("INPUT");
			checkbox.setAttribute("type", "checkbox");
			checkbox.checked=false;
			checkbox.id = val.id;
			checkbox.addEventListener('click', checkbox_handler);
			cell3.appendChild(checkbox);
			i++;
			if(index+1 == arr.length)
			{
				gather_all_events_from_all_facebook_pages();
			}
		});
		let div = document.createElement("div");
		div.id = "div_table";
		div.className = "div_table";
		div.appendChild(table);
		document.body.appendChild(div);	
	};
	let add_remove_by_id = function(email,status)
	{
			let temp_arr = new Array();
			events.forEach(function(val,index,arr)
			{
				if(val.id == email)
				{
					temp_arr.push(val);
				}
			});
			if(status == "addEventSource")
				$('#calendar1').fullCalendar(status,temp_arr);
			else
			{
				$('#calendar1').fullCalendar(status,email);
				//$('#calendar1').fullCalendar('removeEvents',id);
			}
	};
	const local_storage_func = function()
	{
		$.each(localStorage, function(key, value)
		{
			if(document.getElementById(key) != null && value == 1)
			{
				document.getElementById(key).click();
			}
		})
		
		
	};
		
	return { initModule, gather_all_events_from_all_facebook_pages, create_buttons, events,users,build_filter_table,local_storage_func};
	
}());



$( document ).ready(function() 
{
	page_module.create_buttons();
	page_module.initModule();
	//$('#calendar1').fullCalendar('removeEvents',id);
	//console.log(page_module.events);
	//setTimeout(page_module.local_storage_func,1500);
});







