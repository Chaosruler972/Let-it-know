package com.jerusalem_open_house.let_it_know.letitknow;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{

    private Button btn_calview;
    private Button btn_edittable;
    private Button btn_login;
    private Button btn_exit;
    private SQL_DB db = null;
    private boolean rubick = true;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_calview = (Button) findViewById(R.id.btn_menu_viewcal);
        btn_edittable = (Button) findViewById(R.id.btn_menu_edittable);
        btn_login = (Button) findViewById(R.id.btn_menu_login);
        btn_exit = (Button) findViewById(R.id.btn_menu_exit);
        Typeface font = Typeface.createFromAsset(getAssets(), "ktavyad.otf");
        btn_login.setTypeface(font);
        btn_edittable.setTypeface(font);
        btn_calview.setTypeface(font);
        btn_exit.setTypeface(font);

        firebase_services.SyncUsers(getBaseContext());

        db = new SQL_DB(getBaseContext());
        btn_calview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                
                Intent open_cal_view_intent = new Intent(MainActivity.this, calendar.class);
                startActivity(open_cal_view_intent);
            }
        });

        if(!db.check_db_exists(getBaseContext()))
        {
            db.create_table_doesnt_exists(this);
        }
        if(!db.check_db_exists(this))
        {
            btn_calview.setVisibility(View.INVISIBLE);
            btn_edittable.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Error, database doesn't exist, please re-open app", Toast.LENGTH_SHORT).show();
        }


        btn_edittable.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Intent edit_table = new Intent(MainActivity.this, edit_table.class);
                startActivity(edit_table);
            }
        });


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent login_intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(login_intent);
            }
        });



        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                
                finishAffinity();
            }
        });
        new AsyncCaller().execute();
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.about_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.action_about_us:
                Intent intent = new Intent(MainActivity.this,About_us.class);
                startActivity(intent);
                break;
            case R.id.action_log_in:
                btn_login.callOnClick();
                break;
            default:
                break;
        }
        return true;
    }
    public void onBackPressed()
    {
        if(rubick)
            super.onBackPressed();

    }
    private void createNotification(String text, String link)
    {

        NotificationCompat.Builder notificationBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setAutoCancel(true).setSmallIcon(R.drawable.lit1).setContentText(text).setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent resultIntent = new Intent(Intent.ACTION_VIEW);
        if(URLUtil.isValidUrl(link))
          resultIntent.setData(Uri.parse(link));

        PendingIntent pending = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pending);

        int id = (int) Math.random()*1000;
        mNotificationManager.notify(id, notificationBuilder.build());

    }



    public class AsyncCaller extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
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
                    if(getIntent().getStringExtra("active").equals("true"))
                        createNotification(getIntent().getStringExtra("body"),getIntent().getStringExtra("title"));
                }
            });
            rubick = true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
}
