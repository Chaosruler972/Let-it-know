Version 1.6

* implented "Fullcalendar mobile" -> versatile fullcalendar for mobile view
* appended Firefox "e.target" instead of "e.srcelement" on script.js of index.html (calendar page)
* removed "make admin" button (request of client)
* upon user removal, all his manual events are deleted now too
* added location to manual event (duh!)
* added event's location to display
* fixed URL (?)
* Events are loaded per month ago,month ahead basis
* added user website url, linked it to user text on calendar's main page
* Added google calendar's support, but Google Calendar events are not rendering yet
// TODO : get that Google Calendar support working, DESIGN.
* Fixed Google Calendar
* Added full-day event parser for holidays on Google Calendar

Version 1.5.9
* added manual event entry page
* added manual event view
* added manual events to be viewable by user

Version 1.5.8


* configs is now global on root folder
* divided to divs, backgrounded them
* added a new page, didn't edit it at all
* fixed amount of events, ajax stop included for local storage sync





Version 1.5.7


* changed user interface login from email to username

* made changes to db
* local storage by id instead of by number

* nicknames to users
* move admin rights became a button

* added tooltip (small)
* added auto approve




// TODO improve User Interface



Version 1.5.6


* FullCalendar theme was added.




Version 1.5.5


* Added Admin can change passwords

* fixed when admin deletes a user, all his facebook pages are deleted




Version 1.5.4


* fixed callbacks to be function based instead of time based
(//TODO link)

// TODO callbacks for after gather_all_events() and get_users_db() to call localstorage_function to "repick" our last picked username filter



Version 1.5.3


* added local storage to remember last picked users for display filter

* fixed username to deprecete "@domain" suffix
* fixed password length to be >=6




Version 1.5.2
* Fix in date bug.

Version 1.51


* Added filtering to calendar

* Events colored by source

* Admin is capable of choosing colors for users (including himself)

* Moved libraries to libs
* Create new users (only admin)



Version 1.5


* Added Admin-page
* Auth by FireAuth

* Added icons for buttons




Version 1.43


* updated "back button" to image

* fixed hebrew unicode issue with links



Version 1.42


* added back button

* added Load screen for facebook page (?)



Version 1.41


*fix - config.js on /facebook_page variable name was incorrect




Version 1.4



* Autheration by APP id instead of user-token, allowing grasp of event data without logging in

* modulized config javascript in both config files and script files

* hidden app id source with predefined password under module

* hidden token id by app-id in module

// TODO

* Autheration page (login) for Facebook page database



Version 1.3


* added Facebook event database manipulation page

* based configuration file on source

* added Google Firebase operation API to db page

* added viewable table and buttonized operation to table


// TODO

 * Autheration by ID



Version 1.2


* moved token variable to configuration

* added Google Firebase API to API-key configuration on config file

* added firebase-DB support and load

* added dynamic event from facebook load and render


// TODO

* Facebook DB page manipulation page






Version 1.1


* added Facebook API

* added pre-configured JS file to convert Facebook API date to generlized date

* added function to gather events from pre-configured Facebook page

* lists all said event on fullcalendar API
* virtual defiance on index.html to define each list of imports to it's conclusion

* completed skeleton

// TODO

* dynamic loaded Facebook page (/event) loading

* database to load Facebook pages from







First version


* added a virtual div for calendar

* chosen an apporpiate calendar API

* added chosen calendar API to logic and interface operations

* did a skeleton build for apporpiate functions

// TODO


* add facebook API
* add facebook page control page

* add all events from facebook page database to calendar and load them
