package com.jerusalem_open_house.let_it_know.letitknow;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class calendar extends AppCompatActivity
{
    private CaldroidFragment caldroidFragment;
    private  Vector<manual_event> manual_events;
    private Vector<Event_source> sources;
    private Vector<User> users;
    private ListView list;
    private ArrayAdapter<event_data> data;
    private Spinner spinner;
    private Button view;
    private Button back;
    private boolean rubick = true;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        Typeface font = Typeface.createFromAsset(getAssets(), "ktavyad.otf");
        spinner = (Spinner)findViewById(R.id.calendar_spinner);
        view = (Button) findViewById(R.id.calendar_view);
        view.setVisibility(View.INVISIBLE);
        back = (Button)findViewById(R.id.calendar_back);
        back.setTypeface(font);
        view.setTypeface(font);
        anime_class.addClickEffect((View) view);
        anime_class.addClickEffect((View) back);
        back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                
                finish();
            }
        });


        caldroidFragment = new CaldroidFragment();



        Bundle args = new Bundle();
        java.util.Calendar cal = java.util.Calendar.getInstance();;

        args.putInt(CaldroidFragment.MONTH, cal.get(java.util.Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(java.util.Calendar.YEAR));
        caldroidFragment.setArguments(args);

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.commit();

        Bundle args2 = new Bundle();
        args2.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.SUNDAY); // SUNDAY
        caldroidFragment.setArguments(args2);
        new AsyncCaller().execute();
    }
    public void onBackPressed()
    {
        if(rubick)
            super.onBackPressed();

    }
    public void update_events()
    {
        final facebook_helper fb = new facebook_helper();
        sources = firebase_services.event_sources;
        manual_events = firebase_services.manual_events;
        SQL_DB db = new SQL_DB(this);
        users = db.get_users(this);
        if(users.size() == 0)
        {
            Toast.makeText(this, "Empty user database! please check users", Toast.LENGTH_SHORT).show();
            return;
        }
        final Vector<event_data> events = new Vector<>();
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = new JacksonFactory();
        Calendar service = new Calendar.Builder(transport, jsonFactory, null)
                .setApplicationName("applicationName").build();
        String[] acceptedFormats = {"yyyy-MM-dd'T'HH:mm:ssZ","yyyy-MM-dd"};

        for(User user: users)
        {
            if(user.a_aprove)
            {
                if(!firebase_services.getUserbyID(user.id).a_aprove)
                    continue;
                for (Event_source src : sources) {
                    if (src.provider_name.equals("Facebook") && src.user.id.equals(user.id)) {
                        fb.get_events(src.id, events, getIntent(), user, caldroidFragment, getBaseContext());
                    } else if (src.provider_name.equals("Google") && src.user.id.equals(user.id)) {
                        HttpGetRequest req = new HttpGetRequest("https://www.googleapis.com/calendar/v3/calendars/" + src.id + "/events?key=" + this.getResources().getString(R.string.google_calendar_key));
                        String result = "";
                        try {
                            result += req.execute().get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        try {
                            JSONObject main = new JSONObject(result);
                            JSONArray arr = main.getJSONArray("items");
                            for (int i = 0; i < arr.length(); i++) {
                                event_data event = new event_data();
                                event.user = user;
                                event.name = arr.getJSONObject(i).getString("summary");
                                //event.start = df.parse(arr.getJSONObject(i).getJSONObject("start").getString("date"));
                                event.start = DateUtils.parseDate(arr.getJSONObject(i).getJSONObject("start").getString("date"), acceptedFormats);
                                event.end = DateUtils.parseDate(arr.getJSONObject(i).getJSONObject("end").getString("date"), acceptedFormats);
                                if(arr.getJSONObject(i).has("location"))
                                  event.location = arr.getJSONObject(i).getString("location");
                                events.add(event);
                                ColorDrawable color = new ColorDrawable(getResources().getColor(R.color.get_day));
                                int num_of_days = (int) TimeUnit.MILLISECONDS.toDays(Math.abs(event.start.getTime() - event.end.getTime()));
                                Date backup = new Date(event.start.getTime());
                                for (int j = 0; j <= num_of_days; j++)
                                {
                                    if(!DateUtils.isSameDay(backup,new Date()))
                                        caldroidFragment.setBackgroundDrawableForDate(color, backup);
                                    backup.setTime(backup.getTime() + 86400000);
                                }
                                caldroidFragment.refreshView();
                            }


                            Log.d("events", arr.get(1).toString());
                        } catch (JSONException e) {
                            Log.d("Google", e.getMessage().toString());
                            e.printStackTrace();
                        } catch (ParseException e) {
                            Log.d("Google", e.getMessage().toString());
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        data = new ArrayAdapter<event_data>(this,android.R.layout.simple_list_item_1)
        {
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                event_data event = data.getItem(position);
                if (event.user != null)
                { // we're on an even row
                    view.setBackgroundColor(Color.parseColor("#" + event.user.color));
                }
                return view;
            }
        };
        spinner.setAdapter(data);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id)
            {
                view.setVisibility(View.VISIBLE);
                final event_data event = data.getItem(position);
             //   if(event.user != null)
                //    arg1.setBackgroundColor(Color.parseColor("#" + event.user.color));
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(calendar.this, Display_event_data.class);
                        intent.putExtra("event",event);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {
                view.setVisibility(View.INVISIBLE);

            }
        });
        Log.d("test3",manual_events.elementAt(0).user.toString());
        for(manual_event manual_event: manual_events)
        {

            for(User user : users)
            {
                if(user.id.equals(manual_event.user.id) && user.a_aprove)
                {

                    if(!firebase_services.getUserbyID(user.id).a_aprove)
                        continue;
                    Date start = manual_event.start;
                    Date end = manual_event.end;
                    ColorDrawable color = new ColorDrawable(getResources().getColor(R.color.get_day));
                    int num_of_days = (int) TimeUnit.MILLISECONDS.toDays(Math.abs(start.getTime() - end.getTime()));
                    Date backup = new Date(start.getTime());
                    for (int i = 0; i <= num_of_days; i++)
                    {
                        if(!DateUtils.isSameDay(backup,new Date()))
                          caldroidFragment.setBackgroundDrawableForDate(color, backup);
                        backup.setTime(backup.getTime() + 86400000);
                    }

                    caldroidFragment.refreshView();
                    break;
                }
            }
        }



        final CaldroidListener listener = new CaldroidListener()
        {
            private View last_view = null;
            private int last_color;
            @Override
            public void onSelectDate(Date date, View view)
            {
                if(last_view != null)
                    last_view.setBackgroundColor(last_color);
                view.setBackgroundColor(Color.GREEN);
                last_view = view;
                last_color = ((ColorDrawable) view.getBackground()).getColor();
                java.util.Calendar cal1 = java.util.Calendar.getInstance();
                cal1.setTime(date);
                data.clear();
                for(manual_event manual: manual_events)
                {
                    java.util.Calendar cal2 = java.util.Calendar.getInstance();
                    java.util.Calendar cal3 = java.util.Calendar.getInstance();
                    cal2.setTime(manual.start);
                    cal3.setTime(manual.end);
                    boolean sameStart = cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
                            cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR);
                    boolean sameEnd = cal1.get(java.util.Calendar.YEAR) == cal3.get(java.util.Calendar.YEAR) &&
                            cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal3.get(java.util.Calendar.DAY_OF_YEAR);
                    boolean start_or_end = sameStart || sameEnd;
                    if(date.after(manual.start) && date.before(manual.end) || start_or_end)
                    {
                        event_data event = new event_data();
                        event.start = manual.start;
                        event.end = manual.end;
                        event.location = manual.location;
                        event.name = manual.name;
                        if(manual.url != null)
                            event.url = manual.url;
                        for(User user: users)
                        {

                            if(user.id.equals(manual.user.id) && user.a_aprove)
                            {
                                if(!firebase_services.getUserbyID(user.id).a_aprove)
                                    continue;
                                event.user = user;
                                data.add(event);
                            }
                        }

                    }
                }
                for(event_data event : events)
                {
                    java.util.Calendar cal2 = java.util.Calendar.getInstance();
                    java.util.Calendar cal3 = java.util.Calendar.getInstance();
                    cal2.setTime(event.start);
                    cal3.setTime(event.end);
                    boolean sameStart = cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
                            cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR);
                    boolean sameEnd = cal1.get(java.util.Calendar.YEAR) == cal3.get(java.util.Calendar.YEAR) &&
                            cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal3.get(java.util.Calendar.DAY_OF_YEAR);
                    boolean start_or_end = sameStart || sameEnd;
                    if(date.after(event.start) && date.before(event.end) || start_or_end)
                    {
                        data.add(event);
                    }
                }
                Log.d("events",events.size()+"");
            }
        };
        caldroidFragment.setCaldroidListener(listener);
    }

    public class AsyncCaller extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog pdLoading = new ProgressDialog(calendar.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setCancelable(false);
            pdLoading.setMessage("\tטוען...");
            pdLoading.show();
        }
        @Override
        protected Void doInBackground(Void... params) {

            //this method will be running on background thread so don't update UI frome here
            //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here
            rubick = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            });
            while(!firebase_services.done_sync_users)
            {
                try
                {
                    Thread.sleep(1000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            firebase_services srv = new firebase_services();
            srv.SyncProvidors(getBaseContext(),getIntent(),"","");
            firebase_services.Sync_providor(calendar.this,"Facebook");
            firebase_services.Sync_providor(calendar.this,"Google");
            firebase_services.Sync_manual(getBaseContext(),getIntent(),"",true);
            while( getIntent().getStringExtra("Facebook")==null || getIntent().getStringExtra("Google")==null || getIntent().getStringExtra("Manual")==null)
            {
                try
                {
                    Thread.sleep(1000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    update_events();

                }
            });

            rubick = true;
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            //this method will be running on UI thread

            pdLoading.dismiss();
        }

    }

    public class HttpGetRequest extends AsyncTask<String, Void, String> {
        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;
        public String url;
        public HttpGetRequest(String url)
        {
            super();
            this.url = url;
        }
        @Override
        protected String doInBackground(String... params){
//            String stringUrl = params[0];
            String result;
            String inputLine;
            try {
                //Create a URL object holding our url
                URL myUrl = new URL(url);
                //Create a connection
                HttpURLConnection connection =(HttpURLConnection)
                        myUrl.openConnection();
                //Set methods and timeouts
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);

                //Connect to our url
                connection.connect();
                //Create a new InputStreamReader
                InputStreamReader streamReader = new
                        InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();
            }
            catch(IOException e){
                e.printStackTrace();
                result = null;
            }
            return result;
        }
        protected void onPostExecute(String result){
            super.onPostExecute(result);
        }
    }

}
