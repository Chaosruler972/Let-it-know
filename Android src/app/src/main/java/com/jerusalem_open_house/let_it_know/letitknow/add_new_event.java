package com.jerusalem_open_house.let_it_know.letitknow;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class add_new_event extends AppCompatActivity
{

    private EditText text_name;
    private EditText text_location;
    private EditText text_URL;
    private TextView by;
    private EditText start_time;
    private EditText end_time;
    private EditText start_date;
    private EditText end_date;
    private Button add_btn;
    private Button back;


    private boolean btn_bool1 = false;
    private boolean btn_bool2 = false;
    private boolean btn_bool3 = false;
    private boolean btn_bool4 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_event);

        back = (Button)findViewById(R.id.new_event_back);

        text_name = (EditText)findViewById(R.id.add_new_name);
        text_location = (EditText)findViewById(R.id.add_new_event_location);
        text_URL = (EditText) findViewById(R.id.new_event_URL);
        by = (TextView)findViewById(R.id.new_event_by);
        add_btn = (Button) findViewById(R.id.add_new_event_add);

        start_date = (EditText) findViewById(R.id.add_event_start_date);
        end_date = (EditText) findViewById(R.id.add_event_end_date);
        start_time = (EditText) findViewById(R.id.new_event_start_time);
        end_time = (EditText) findViewById(R.id.new_event_end_time);
        Typeface font = Typeface.createFromAsset(getAssets(), "ktavyad.otf");
        back.setTypeface(font);
        add_btn.setTypeface(font);

        by.setText(firebase_services.getuser().getEmail().substring(0,firebase_services.getuser().getEmail().indexOf('@')));

        start_date.setInputType(InputType.TYPE_NULL);
        end_date.setInputType(InputType.TYPE_NULL);
        start_time.setInputType(InputType.TYPE_NULL);
        end_time.setInputType(InputType.TYPE_NULL);

        final String start_date_str = "";
        final String end_date_str = "";

        setDate start_d = new setDate(start_date, this,start_date_str);
        setDate end_d = new setDate(end_date, this,end_date_str);

        start_time.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                
                final Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(add_new_event.this, new TimePickerDialog.OnTimeSetListener() {
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
                        btn_bool4 = true;
                        if(status())
                            add_btn.setVisibility(View.VISIBLE);
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
                mTimePicker = new TimePickerDialog(add_new_event.this, new TimePickerDialog.OnTimeSetListener() {
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
                        btn_bool3 = true;
                        if(status())
                            add_btn.setVisibility(View.VISIBLE);
                    }

                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });


        back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
                Intent add_new_event = new Intent(add_new_event.this,add_manual_event.class);
                startActivity(add_new_event);
            }
        });

        add_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                
                if(text_name.getText().toString().length() == 0 || text_location.getText().toString().length()==0)
                    return;
                try
                {
                    Date sdate = new SimpleDateFormat("dd MM, yyyy", Locale.US).parse(start_date.getText().toString());
                    Date edate = new SimpleDateFormat("dd MM, yyyy", Locale.US).parse(end_date.getText().toString());
                    manual_event event = new manual_event(text_name.getText().toString(),text_location.getText().toString(),  sdate.getTime() ,start_time.getText().toString(), edate.getTime()  ,end_time.getText().toString(),text_URL.getText().toString());
                    for(User user : firebase_services.users)
                    {
                        if(user.id.equals(by.getText().toString()))
                            event.user = user;
                    }
                    if(event.user.id == null)
                        return;
                    firebase_services.add_manual_event(getBaseContext(),event);
                    firebase_services.manual_events.add(event);
                }
                catch (ParseException e)
                {
                    Log.d("test",e.getMessage().toString());
                    e.printStackTrace();
                }
                back.callOnClick();
            }
        });
    }
    public void onBackPressed()
    {
        back.callOnClick();

    }
    private boolean status()
    {
        return btn_bool1 && btn_bool2 && btn_bool3 && btn_bool4;
    }

    class setDate implements View.OnFocusChangeListener, DatePickerDialog.OnDateSetListener
    {
        private EditText editText;
        private Calendar myCalendar;
        private Context ctx;
        String input;

        public setDate(EditText editText, Context ctx, String input)
        {
            this.editText = editText;
            this.editText.setOnFocusChangeListener(this);
            myCalendar = Calendar.getInstance();
            this.input = input;
            this.ctx = ctx;
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
            if(editText.equals(start_date))
            {
                btn_bool1 = true;
                if(status())
                    add_btn.setVisibility(View.VISIBLE);
            }
            else if(editText.equals(end_date))
            {
                btn_bool2 = true;
                if(status())
                    add_btn.setVisibility(View.VISIBLE);
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
