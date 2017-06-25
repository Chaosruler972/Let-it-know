package com.jerusalem_open_house.let_it_know.letitknow;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.TextView;

public class Display_event_data extends AppCompatActivity
{
    private TextView name;
    private TextView location;
    private TextView date_start;
    private TextView date_end;
    private TextView URL;
    private TextView by;
    private Button back;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_event_data);
        back = (Button) findViewById(R.id.display_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                
                finish();
            }
        });
        name = (TextView) findViewById(R.id.display_name);
        location = (TextView) findViewById(R.id.display_location);
        date_start = (TextView) findViewById(R.id.display_start);
        date_end = (TextView) findViewById(R.id.display_end);
        URL = (TextView) findViewById(R.id.display_URL);
        by = (TextView) findViewById(R.id.display_by);
        Typeface font = Typeface.createFromAsset(getAssets(), "ktavyad.otf");
        back.setTypeface(font);

       // event_data event = (event_data) getIntent().getSerializableExtra("event");
        final event_data event = (event_data) getIntent().getParcelableExtra("event");
        Log.d("event_parceable",event.toString());
        if(event == null)
            finish();
        /*
        if(event.user != null) {
            int c = Color.parseColor("#" + event.user.color);
            name.setTextColor(c);
            location.setTextColor(c);
            date_start.setTextColor(c);
            date_end.setTextColor(c);
            URL.setTextColor(c);
            by.setTextColor(c);
        }
        */
        name.setText("שם: " + event.name);
        date_start.setText("זמן התחלה: " + event.start.toString());
        date_end.setText("זמן סוף: " + event.end.toString());
        if(event.url != null)
        {
            URL.setText("קישור: " + event.url);
            URL.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(URLUtil.isValidUrl(event.url))
                    {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.url));
                        startActivity(browserIntent);
                    }
                }
            });
            URL.setPaintFlags(URL.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            URL.setTextColor(Color.BLUE);
        }
        else
            URL.setText("לא ידוע קישור לארוע זה");
        if(event.location!=null)
            location.setText("כתובת: " + event.location);
        else
            location.setText("לא ידוע כתובת לארוע זה.");
        if(event.user != null)
          by.setText("מגיש הארוע: " + event.user.name);

        name.setTypeface(font);
        location.setTypeface(font);
        URL.setTypeface(font);
        by.setTypeface(font);
        date_start.setTypeface(font);
        date_end.setTypeface(font);


    }
}
