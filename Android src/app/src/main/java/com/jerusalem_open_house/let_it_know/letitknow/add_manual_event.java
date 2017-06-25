package com.jerusalem_open_house.let_it_know.letitknow;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import static android.R.layout.simple_spinner_item;

public class add_manual_event extends AppCompatActivity
{
    private Button back;
    private Button add_event;
    private Spinner spinner;
    private ArrayAdapter<manual_event> adapter;
    private EditText location;
    private Button change_location;
    private TextView by;
    private EditText start_date;
    private EditText start_time;
    private EditText end_date;
    private EditText end_time;
    private Button delete;
    private EditText url;
    private Button change_url;
    private String admin;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_manual_event);

        back = (Button)findViewById(R.id.add_manual_back);
        add_event = (Button)findViewById(R.id.add_manual_event);
        spinner = (Spinner)findViewById(R.id.add_manual_spinner);
        location = (EditText)findViewById(R.id.add_manual_locatoin);
        change_location = (Button)findViewById(R.id.add_manual_change_location);
        by = (TextView)findViewById(R.id.manual_event_by);
        start_date = (EditText)findViewById(R.id.add_manual_start_date);
        start_time = (EditText)findViewById(R.id.add_manual_start_time);
        end_date = (EditText)findViewById(R.id.add_manual_end_date);
        end_time = (EditText)findViewById(R.id.add_manual_end_time);
        delete = (Button)findViewById(R.id.add_manual_delete);
        url = (EditText)findViewById(R.id.manual_event_url);
        change_url = (Button)findViewById(R.id.add_manual_url);


        start_date.setInputType(InputType.TYPE_NULL);
        end_date.setInputType(InputType.TYPE_NULL);
        start_time.setInputType(InputType.TYPE_NULL);
        end_time.setInputType(InputType.TYPE_NULL);


        Typeface font = Typeface.createFromAsset(getAssets(), "ktavyad.otf");
        back.setTypeface(font);
        add_event.setTypeface(font);
        change_location.setTypeface(font);
        delete.setTypeface(font);
        change_url.setTypeface(font);

        back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                
                finish();
                Intent intent = new Intent(add_manual_event.this,add_remove_ids.class);
                startActivity(intent);
            }
        });

        add_event.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
                Intent add_new_event = new Intent(add_manual_event.this,add_new_event.class);
                startActivity(add_new_event);
            }
        });

        new AsyncCaller_events().execute();
    }
    public void onBackPressed()
    {
        back.callOnClick();

    }
    public void onActivityResult(int requestCode, int resultCode, Intent in)
    {
        //super(requestCode,resultCode,in);
        adapter.clear();
        String userid = firebase_services.getuser().getEmail().substring(0,firebase_services.getuser().getEmail().indexOf('@'));
        Vector<manual_event> events = firebase_services.manual_events;
        for(manual_event event: events)
        {
            if(userid.equals(event.user.id) || userid.equals(admin))
                adapter.add(event);
        }
        adapter.notifyDataSetChanged();
    }

    public void update_table()
    {
        Vector<manual_event> events = firebase_services.manual_events;
        ArrayList<manual_event> event_list = new ArrayList<manual_event>();
        String userid = firebase_services.getuser().getEmail().substring(0,firebase_services.getuser().getEmail().indexOf('@'));
        for(manual_event event: events)
        {
            if(userid.equals(event.user.id) || userid.equals(admin))
                 event_list.add(event);
        }
        if(userid.equals(admin))
            add_event.setVisibility(View.VISIBLE);
        Log.d("admin",admin + "," + userid);
        adapter = new ArrayAdapter<manual_event>(this, simple_spinner_item, event_list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id)
            {
                change_url.setVisibility(View.VISIBLE);
                change_location.setVisibility(View.VISIBLE);
                start_time.setVisibility(View.VISIBLE);
                start_date.setVisibility(View.VISIBLE);
                end_date.setVisibility(View.VISIBLE);
                end_time.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
                final manual_event event = adapter.getItem(position);
                location.setText(event.location);
                change_location.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        
                        if(location.getText().toString().length() == 0)
                            return;
                        manual_event event2 = new manual_event(event);
                        event2.location = location.getText().toString();
                        firebase_services.update_manual_event(getBaseContext(),event,event2);
                        event.location = location.getText().toString();
                    }
                });
                if(event.url != null)
                    url.setText(event.url + " ");
                change_url.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        
                        if(url.getText().toString().length() == 0)
                            return;
                        manual_event event2 = new manual_event(event);
                        event2.url = url.getText().toString();
                        firebase_services.update_manual_event(getBaseContext(),event,event2);
                        event.url = url.getText().toString();
                    }
                });
                delete.setVisibility(View.VISIBLE);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        
                        firebase_services.update_manual_event(getBaseContext(),event,null);
                        adapter.remove(event);
                        if(adapter.isEmpty())
                            delete.setVisibility(View.INVISIBLE);
                        adapter.notifyDataSetChanged();
                    }
                });
                String start_date_input = new String();
                String end_date_input = new String();

                Calendar sc = Calendar.getInstance();
                sc.setTime(event.start);

                Calendar ec = Calendar.getInstance();
                ec.setTime(event.end);

                String str = "";
                if(sc.get(Calendar.HOUR_OF_DAY) < 10)
                    str+="0";
                str+="" + sc.get(Calendar.HOUR_OF_DAY);
                str+=":";
                if(sc.get(Calendar.MINUTE) < 10)
                    str+="0";
                str+="" + sc.get(Calendar.MINUTE);
                start_time.setText(str);

                str = "";
                if(ec.get(Calendar.HOUR_OF_DAY) < 10)
                    str+="0";
                str+="" + ec.get(Calendar.HOUR_OF_DAY);
                str+=":";
                if(ec.get(Calendar.MINUTE) < 10)
                    str+="0";
                str+="" + ec.get(Calendar.MINUTE);
                end_time.setText(str);

                String myFormat = "dd MM yyyy"; //In which you need put here
                SimpleDateFormat format = new SimpleDateFormat(myFormat, Locale.US);

                start_date.setText(format.format(sc.getTime()));
                end_date.setText(format.format(ec.getTime()));

                setDate start_d = new setDate(start_date,add_manual_event.this,start_date_input,event);
                setDate end_d = new setDate(end_date,add_manual_event.this,end_date_input,event);

                start_time.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        
                        Calendar mcurrentTime = Calendar.getInstance();
                        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                        int minute = mcurrentTime.get(Calendar.MINUTE);
                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(add_manual_event.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                            {
                                String str = "";
                                if(hourOfDay < 10)
                                    str+="0";
                                str+="" + hourOfDay;
                                str+=":";
                                if(minute < 10)
                                    str+="0";
                                str+="" + minute;
                                start_time.setText( str);
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(event.start);
                                cal.set(Calendar.HOUR,hourOfDay);
                                cal.set(Calendar.MINUTE,minute);
                                manual_event event2 = new manual_event(event);
                                event2.start = cal.getTime();
                                firebase_services.update_manual_event(getBaseContext(),event,event2);
                                event.start = cal.getTime();
                            }

                        }, hour, minute, true);//Yes 24 hour time
                        mTimePicker.setTitle("Select Time");
                        mTimePicker.show();
                    }
                });

                end_time.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        
                        Calendar mcurrentTime = Calendar.getInstance();
                        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                        int minute = mcurrentTime.get(Calendar.MINUTE);
                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(add_manual_event.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                            {
                                String str = "";
                                if(hourOfDay < 10)
                                    str+="0";
                                str+="" + hourOfDay;
                                str+=":";
                                if(minute < 10)
                                    str+="0";
                                str+="" + minute;
                                end_time.setText( str);
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(event.end);
                                cal.set(Calendar.HOUR,hourOfDay);
                                cal.set(Calendar.MINUTE,minute);
                                manual_event event2 = new manual_event(event);
                                event2.end = cal.getTime();
                                firebase_services.update_manual_event(getBaseContext(),event,event2);
                                event.end = cal.getTime();
                            }

                        }, hour, minute, true);//Yes 24 hour time
                        mTimePicker.setTitle("Select Time");
                        mTimePicker.show();
                    }
                });


            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {
                change_url.setVisibility(View.INVISIBLE);
                change_location.setVisibility(View.INVISIBLE);
                start_time.setVisibility(View.INVISIBLE);
                start_date.setVisibility(View.INVISIBLE);
                end_date.setVisibility(View.INVISIBLE);
                end_time.setVisibility(View.INVISIBLE);
                delete.setVisibility(View.INVISIBLE);
                location.setText("");
                by.setText("");
                start_date.setText("");
                start_time.setText("");
                end_date.setText("");
                end_time.setText("");
                url.setText("");



            }
        });

    }

    public class AsyncCaller_events extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog pdLoading = new ProgressDialog(add_manual_event.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setCancelable(false);
            pdLoading.setMessage("\tטוען...");
            pdLoading.show();
        }
        @Override
        protected Void doInBackground(Void... params)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            });

            //this method will be running on background thread so don't update UI frome here
            //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here
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
            runOnUiThread
                    (
                            new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    add_event.setVisibility(View.VISIBLE);
                                }
                            });

            firebase_services.get_admin(getBaseContext(),getIntent());
            while(getIntent().getStringExtra("admin")==null)
            {
                try
                {
                    Thread.sleep(1000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            boolean isadmin=false;
            if(getIntent().getStringExtra("admin").toString().equals(firebase_services.getuser().getEmail().substring(0,firebase_services.getuser().getEmail().indexOf('@'))) )
                isadmin=true;
            admin = getIntent().getStringExtra("admin").toString();
            firebase_services.Sync_manual(getBaseContext(),getIntent(),firebase_services.getuser().getEmail().substring(0,firebase_services.getuser().getEmail().indexOf('@')),isadmin);
            while(getIntent().getStringExtra("Manual")==null)
            {
                try
                {
                    Thread.sleep(1000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            runOnUiThread
            (
               new Runnable()
                {
                        @Override
                        public void run()
                        {
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            update_table();
                        }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            //this method will be running on UI thread

            pdLoading.dismiss();
        }

    }

    class setDate implements View.OnFocusChangeListener, DatePickerDialog.OnDateSetListener
    {
        private EditText editText;
        private Calendar myCalendar;
        private Context ctx;
        String input;
        manual_event event;

        public setDate(EditText editText, Context ctx, String input,manual_event event)
        {
            this.editText = editText;
            this.editText.setOnFocusChangeListener(this);
            myCalendar = Calendar.getInstance();
            this.input = input;
            this.ctx = ctx;
            this.event = event;
        }

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            // this.editText.setText();

            String myFormat = "dd MM, yyyy"; //In which you need put here
            SimpleDateFormat sdformat = new SimpleDateFormat(myFormat, Locale.US);
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            editText.setText(sdformat.format(myCalendar.getTime()));
            this.input += myCalendar.getTime();
            EditText time = null;
            if(editText == start_date)
            {
                time = start_time;
            }
            else if(editText == end_date)
            {
                time=end_time;
            }
            long date_long = myCalendar.getTime().getTime();
            int hour = Integer.parseInt(time.getText().toString().substring(0,time.getText().toString().indexOf(':')));
            int minute = Integer.parseInt(time.getText().toString().substring(0,time.getText().toString().indexOf(':')));
            date_long += (hour)*3600000 + (minute)*60000;
            Date date = new Date(date_long);
            manual_event event2 = new manual_event(event);
            if(editText == start_date)
            {
                event2.start = date;
            }
            else if(editText == end_date)
            {
                event2.end = date;
            }
            firebase_services.update_manual_event(getBaseContext(),event,event2);
            if(editText == start_date)
            {
                event.start = date;
            }
            else if(editText == end_date)
            {
                event.end = date;
            }

        }
        public void onFocusChange(View v, boolean hasFocus)
        {

            if(hasFocus)
            {
                new DatePickerDialog(ctx, this, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        }

    }
}
