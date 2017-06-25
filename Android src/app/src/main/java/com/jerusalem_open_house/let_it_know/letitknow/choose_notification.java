package com.jerusalem_open_house.let_it_know.letitknow;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;


public class choose_notification extends AppCompatActivity
{

    private Button back;
    private Button update;
    private Button erase_body;
    private Button erase_title;
    private CheckBox check;
    private EditText title;
    private EditText body;
    private boolean rubick = true;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_notification);

        back = (Button) findViewById(R.id.notification_back);
        update = (Button)findViewById(R.id.notification_update);
        erase_body = (Button)findViewById(R.id.notification_clear_body);
        erase_title = (Button) findViewById(R.id.notification_clear_title);
        check = (CheckBox) findViewById(R.id.notification_active);
        title = (EditText)findViewById(R.id.notification_title);
        body = (EditText)findViewById(R.id.notification_body);
        Typeface font = Typeface.createFromAsset(getAssets(), "ktavyad.otf");
        update.setTypeface(font);
        back.setTypeface(font);
        erase_body.setTypeface(font);
        erase_title.setTypeface(font);
        erase_body.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                
                body.setText("");
            }
        });

        erase_title.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                
                title.setText("");
            }
        });

        back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
                Intent intent = new Intent(choose_notification.this,add_remove_ids.class);
                startActivity(intent);
            }
        });
        new AsyncCaller().execute();
    }
    public void onBackPressed()
    {
        if(rubick)
            super.onBackPressed();

    }
    public void update_everything()
    {
        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {

                new AsyncCaller2().execute();
                firebase_services.set_active_inactive_notification(getBaseContext(),getIntent(),isChecked);
            }
        });

        update.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                
                new AsyncCaller2().execute();
                firebase_services.update_notification(getBaseContext(),body.getText().toString(),title.getText().toString(),getIntent());
            }
        });
    }
    public class AsyncCaller extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog pdLoading = new ProgressDialog(choose_notification.this);

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
            rubick = false;
            firebase_services.get_notification(getBaseContext(),getIntent());
            while(getIntent().getStringExtra("title")== null || getIntent().getStringExtra("body")== null || getIntent().getStringExtra("active")==null)
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
                    if(getIntent().getStringExtra("title").equals("null"))
                        title.setText("");
                    else
                        title.setText(getIntent().getStringExtra("title"));
                    if(getIntent().getStringExtra("body").equals("null"))
                        body.setText("");
                    else
                        body.setText(getIntent().getStringExtra("body"));
                    if(getIntent().getStringExtra("active").equals("false"))
                        check.setChecked(false);
                    else
                        check.setChecked(true);
                    update_everything();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            });
            rubick =true;
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            //this method will be running on UI thread

            pdLoading.dismiss();
        }

    }
    public class AsyncCaller2 extends AsyncTask<Void, Void, Void>
    {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread

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
            firebase_services.get_notification(getBaseContext(),getIntent());
            while(getIntent().getStringExtra("Done")==null)
            {
                try
                {
                    Thread.sleep(1000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            getIntent().removeExtra("Done");
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Toast.makeText(choose_notification.this, "Updated!", Toast.LENGTH_SHORT).show();
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            //this method will be running on UI thread

        }

    }
}
