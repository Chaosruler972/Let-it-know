var page_module = (function ()
{
	let facebook_app_id = "1888264188125920";
	let pw = "Mgo6B97nHZKdwXMiGw5P";
	let events = []; // where ALL the events will be
	let users = []; // where all the users info will be
	let google = [];
	let fb = 0; //
	const initModule = function()
	{
		//// Calendar initation virtual space ////
		build_filter_table();
		get_users_db();
		let elemDiv = document.createElement('div'); // limits the calendar UI into a div named calendar1
		elemDiv.id = "calendar1"
		elemDiv.className = "calendar1";
		const speculated_height = window.innerHeight * 0.70; // 70% height
		document.body.appendChild(elemDiv);
		  $('#calendar1').fullCalendar(
		{
		height:  speculated_height,
		header: { // defines the header of the calendar (buttons)
		  left: 'prev,next,today',
		  center: 'title', // which month/year we are in
		  right: 'month,agendaWeek,agendaDay'
		  },
		  theme: true,
		  eventRender: function(event, element) // generates pop up info about the event
		  {
			element.attr('title', event.tooltip);
		  },
		 eventClick: function(event) // on click event handler, uses event object
		 {
			  if (event.url)
			  {
				window.open(event.url);
				return false;
			  }
		 },
		 
		
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


	const gather_all_events_from_all_facebook_pages = function()
	{
		// subdibision into local function!

		const add_event_to_calendar = function(id, name, date, email, enddate,location, source,url) // subfunction responisble for adding event to fullcalendar API event and render that
		{
			let color = "#";
			users.forEach(function(val)
			{
				if(val.id == email)
					color+=val.color;
				return;
			});
			let event;
			if(source == "Facebook")
				event = {id: email , title: name , start: date, end:enddate , url: 'https://www.facebook.com/events/'+id , backgroundColor: color, event_source: email, tooltip: name, location, allDay:false,};
			else if(source == "Manual")
				event = {id: email , title: name , start: date, end:enddate , url: url , backgroundColor: color, event_source: email, tooltip: name, location, allDay:false,};
			else if(source == "Google")
				event = {id: email , title: name , start: date, end:enddate , backgroundColor: color, event_source: email, tooltip: name, location,allDay:false,};
			events.push(event);
		};

		const withdraw_events_from_facebook_page = function(data, email) // subfunction responsible for scanning entire event database file from facebook page
		{
			 // subfunction to print results into Fullcalendar API
			let i;
			for(i=0; i<data.length; i++)
			{
				let event_date = new Date(data[i].start_time); // sets a new Date for event date formating to be dynamic
				//event_date.setISO8601(data[i].start_time); // converts time from Facebook API date format to Javascript Date format
				let end_date;
				if(data[i].end_time)
					end_date= new Date(data[i].end_time);
				let place_name;
				if(data[i].place)
					place_name = data[i].place.name;
				else
					place_name = null;
				add_event_to_calendar(data[i].id, data[i].name, event_date, email, end_date, place_name ,"Facebook");
			}
		};

		const page_through_all_events_from_response = function(response,email)
		{
			withdraw_events_from_facebook_page(response.data,email); // adds all the event from THIS page to the event array
			 if(response.paging.next) // if there's a next page
			 {
				$.getJSON(response.paging.next, function(next_data) // recursive call to gather the events from the next page (until done)
				{
					page_through_all_events_from_response(next_data,email);
				});
			}
		};

		const add_events_from_page = function(str, email) // subfunction responsible for doing the API request for the event database
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
					  alert(response.error);
				  }
				}
			);

		};
		const scan_for_direct_events = function() // manual event entry - database scanner
		{
			let database = firebase.database();
			let leadsRef = database.ref('Events'); // gets to database /Events folder
			leadsRef.once('value',function(snapshot)
			{
				snapshot.forEach(function(childSnapshot)
				{
					if(childSnapshot.val().approve == 1) // add each child that is "approved" by admin to the event database
					{
						add_event_to_calendar(childSnapshot.val().id, childSnapshot.val().name, new Date(childSnapshot.val().start_time),
						 childSnapshot.val().id, new Date(childSnapshot.val().end_time),childSnapshot.val().location, "Manual",childSnapshot.val().url);
					}
				});

			});
		};
		const scan_all_db_for_pages = function() // subfunction responsible for facebook DB load via Firebase API call
		{
			let database = firebase.database();
			let leadsRef = database.ref('Facebook'); // go to /Facebook folder on database
			leadsRef.once('value',function(snapshot)
			{
				snapshot.forEach(function(childSnapshot)
				{
					if(childSnapshot.val().charAt(0) == '1') // adds each child that is approved by admin (child being a facebook page)
						add_events_from_page(childSnapshot.key,childSnapshot.val().substring(1, childSnapshot.val().length));
				});

			}).then(scan_google_calenders);
		};
		
		scan_for_direct_events(); // add manual events
		scan_all_db_for_pages(); // adds all events from facebook database
		$(document).ajaxStop(function ()
		{
			local_storage_func(); // "filters" the rendering of the events on the page from the last picked configuration
			  // 0 === $.active
		});
	};
	const create_buttons = function() // creates the navigation buttons
	{

		let div = document.createElement("div"); // div for navigation buttons
		div.id = "top_buttons_div";
		div.className = "top_buttons_div";
		const back_button_build = function()
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
		back_button_build(); // defined it into functions for future possibility for modularity
	};
	const get_users_db = function() // scans the entire user database to create users array
	{
		let database = firebase.database();
		let leadsRef = database.ref('Users'); // get to Users folder on firebase db
		leadsRef.once('value',function(snapshot)
		{
			snapshot.forEach(function(childSnapshot)  // build user array while each user object consists of user id and user color
			{
				let email = childSnapshot.key;
				let obj = childSnapshot.val();
				let user_obj = new Object(); // create the user "object"
				user_obj.id = email;
				user_obj.key = childSnapshot.key;
				user_obj.color = obj.color;
				user_obj.url = obj.url;
				users.push(user_obj); // adds to array
			});

		}).then(function()
		{
			database.ref("Names").once('value',function(snapshot2) // appends user nickname to user id at users array
			{
				snapshot2.forEach(function(childSnapshot)
				{
					users.forEach(function(val,index,arr)
					{
						if(val.id == childSnapshot.key)
							val.name = childSnapshot.val(); // actual appending call
					});
				});
			}).then(render_table_content) // appends user data to GUI table
		}) // scan_google_calenders

	};
	const scan_google_calenders = function()
	{
			const add_event_to_calendar = function(id, name, date, email, enddate, source,url) // subfunction responisble for adding event to fullcalendar API event and render that
			{
				let color = "#";
				users.forEach(function(val)
				{
					if(val.id == email)
						color+=val.color;
					return;
				});
				
				let event;
				if(enddate == "Full day")
				{
					event = {id: email , title: name , start: date , backgroundColor: color, event_source: email, tooltip: name,url: url, allDay:true};
				}
				else
				{
					event = {id: email , title: name , start: date, end:enddate , backgroundColor: color, event_source: email, tooltip: name,url: url};
				}
				events.push(event);
			};

			let database = firebase.database();
			let leadsRef = database.ref('Google'); // go to /Facebook folder on database
			leadsRef.once('value',function(snapshot)
			{
				snapshot.forEach(function(childSnapshot)
				{
					let obj = childSnapshot.val();
					users.forEach(function(val)
					{
						if(obj.id == val.id)
						{
							obj.color = val.color;
							obj.name = val.name;
							google.push(obj);
						}
					});
				});

			}).then(function()
			{
				google.forEach(function(val)
				{
						
						var calendarid = val.val; // will look somewhat like 3ruy234vodf6hf4sdf5sd84f@group.calendar.google.com

						$.ajax({
							type: 'GET',
							url: 'https://www.googleapis.com/calendar/v3/calendars/' + calendarid+ '/events?key=' + config_module.google_key,
							dataType: 'json',
							success: function (response) 
							{
								response.items.forEach(function(event)
								{
			
									let s = new Date(event.start.dateTime);
									if(s.toString() == 'Invalid Date' && event.summary)
									{
										add_event_to_calendar(val.id, event.summary, new Date(event.start.date), val.id, "Full day" ,null, event.htmlLink);
									}
									else
									{
										let e = new Date(event.end.dateTime);
										if(event.summary)
											add_event_to_calendar(val.id, event.summary, s, val.id, e ,null, event.htmlLink);
									}
								});
								
								//do whatever you want with each
							},
							error: function (response) 
							{
								//console.log(response);
								//tell that an error has occurred
							}
						});	
				});
				
				
			});
	};
	const build_filter_table = function()
	{
		let table = document.createElement("table"); // builds a new table
		table.id = "table";
		let main_row = table.insertRow(0); // creates the first line which on each cell we define the cell's content on different lines
		let main_cell1 = main_row.insertCell(0);
		main_cell1.innerHTML = "E-mail/Name";
		let main_cell2 = main_row.insertCell(1);
		main_cell2.innerHTML = "Color";
		let div = document.createElement("div");
		div.id = "div_table";
		div.className = "div_table";
		div.appendChild(table);
		document.body.appendChild(div);
	};
	const render_table_content = function()
	{
		const checkbox_handler = function(e) // function is responisble for alerting between "filter" and "unfilter" state according to checkbox value
		{
			let status = "";
			let element = e.srcElement || e.target;
			if(element.checked == true)
			{
				status="addEventSource"; // "command" to do
				localStorage.setItem(element.id,"1");
			}
			else
			{
				status = "removeEvents"; // "command" to do
				localStorage.setItem(element.id,"0");
			}
			add_remove_by_id(element.id,status);
		};
		let i=1;
		let table = document.getElementById("table");  // adds data to table
		users.forEach(function(val,index,arr)
		{
			let row = table.insertRow(i);
			let cell1 = row.insertCell(0);
			let user_nick = "";
			if(val.name == null) // will be either username or nickname (if exists), nickname having the apperhand
				user_nick+= val.id;
			else
				user_nick+= val.name;
			if(val.url.length > 0)
				cell1.innerHTML = "<a href='" +val.url+ "'>" + user_nick + "</a>";
			else
				cell1.innerHTML = user_nick;
			let cell2 = row.insertCell(1); // "color" sample cell
			cell2.style.backgroundColor = "#" + val.color; // color defination
			let cell3 = row.insertCell(2);
			let checkbox = document.createElement("INPUT"); // dummy function box with the aforementioned event handler to switch between states
			checkbox.setAttribute("type", "checkbox");
			checkbox.checked=false;
			checkbox.id = val.id;
			checkbox.addEventListener('click', checkbox_handler);
			cell3.appendChild(checkbox);
			i++;
			if(index+1 == arr.length) // will actually render the events (not just from facebook pages, but from everything) after completing the table with all the data
			{
				gather_all_events_from_all_facebook_pages();
			}
		});
		

	};
	const add_remove_by_id = function(email,status) // status being the command to do, email is the id
	{
			let temp_arr = new Array();
			let month_ago = new Date(moment().subtract(1, 'months'));
			let month_ahead = new Date(moment().add(1, 'months'));
			events.forEach(function(val,index,arr) // creates a temproary array that holds all the events that are linked to the user with id==email
			{
				if(val.id == email && val.start.valueOf() > month_ago.valueOf() && val.start.valueOf() < month_ahead.valueOf() )
				{
					temp_arr.push(val);
				}
			});
			if(status == "addEventSource") // if the command was to add those events
			{
				$('#calendar1').fullCalendar(status,temp_arr); // we add those events from the array	
			}
			else
			{
				$('#calendar1').fullCalendar(status,email); // full calendar API special function that handles "remove event" rendering
				//$('#calendar1').fullCalendar('removeEvents',id);
			}
	};
	const local_storage_func = function() // reads the local storage for previous table filtering "configuration" to apply them
	{
		$.each(localStorage, function(i) // reads ALL the local storage values
		{
			if(document.getElementById(localStorage.key(i)) != null && localStorage.getItem(localStorage.key(i)) == 1) // if value is 1, we grab the key as source id
			{
				document.getElementById(localStorage.key(i)).click(); // and applies "click" emulation
			}
		})


	};
	return { initModule, gather_all_events_from_all_facebook_pages, create_buttons, events,users,build_filter_table,local_storage_func,google};

}());



$( document ).ready(function()
{
	page_module.create_buttons(); // first move -> creates the navigation buttons
	page_module.initModule(); // second move, make EVERYTHING, snowball function
	//$('#calendar1').fullCalendar('removeEvents',id);
	//console.log(page_module.events);
	//setTimeout(page_module.local_storage_func,1500);
});
