package com.jerusalem_open_house.let_it_know.letitknow;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Vector;

public class edit_table extends AppCompatActivity
{
    private Button back;
    private TableLayout table;
    private Vector<User> users;
    private boolean rubick = true;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_table);

        back = (Button) findViewById(R.id.edit_table_back);
        table = (TableLayout)findViewById(R.id.edit_table_table);
        Typeface font = Typeface.createFromAsset(getAssets(), "ktavyad.otf");
        back.setTypeface(font);
        back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                
                finish();
            }
        });

        create_meta_row();

        new AsyncCaller().execute();
    }
    public void onBackPressed()
    {
        if(rubick)
            super.onBackPressed();

    }
    public void create_meta_row()
    {
        TableRow row = new TableRow(this);
        TextView title = new TextView(this);
        title.setText(" Organization ");

        TextView color = new TextView(this);
        color.setText(" Color ");


        row.addView(title);
        row.addView(color);
        table.addView(row);

    }

    public void update_table()
    {
        final SQL_DB db = new SQL_DB(getBaseContext());
        Vector<User> users = db.get_users(getBaseContext());
        Vector<User> server_users = firebase_services.users;
        for(int i=0; i<users.size(); i++)
        {
            final User user = users.elementAt(i);
            int j=0;
            for(j=0; j<server_users.size(); j++)
            {
                if(server_users.elementAt(j).id.equals(user.id))
                    break;
            }
            if(j==server_users.size())
                continue;
            TableRow row = new TableRow(this);

            TextView name = new TextView(this);
            name.setText(user.name);
            name.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);

            if(user.url!= null)
            {
                name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        String url = new String();
                        url+=user.url;
                        if (!url.startsWith("http://") && !url.startsWith("https://"))
                            url = "http://" + url;
                        if(URLUtil.isValidUrl(url))
                        {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(browserIntent);
                        }
                    }
                });
            }

            Button color_sample = new Button(this);
            color_sample.setBackgroundColor(Color.parseColor("#" + user.color));

            final CheckBox check = new CheckBox(this);
            check.setChecked(user.a_aprove);
            check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    db.change_user_check(getBaseContext(),user,isChecked);
                }
            });
            row.addView(name);
            row.addView(color_sample);
            row.addView(check);
            table.addView(row);

        }
    }


    public class AsyncCaller extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog pdLoading = new ProgressDialog(edit_table.this);

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
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    update_table();

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
}
