package com.jerusalem_open_house.let_it_know.letitknow;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static android.R.id.list;
import static android.R.layout.simple_spinner_item;

public class add_remove_ids extends AppCompatActivity
{

    private Button exit;
    private Button add_manual;
    private Button manage_users;
    private Button submit_website;
    private EditText url;
    private Spinner spinner;
    private TextView by;
    private TextView status;
    private Button delete;
    private TextView event_url;

    private Button notification_button;
    private Button facebook_btn;
    private Button google_btn;
    private ImageView proiv_icon;

    private String admin = null;
    private boolean rubick = true;
    private ArrayAdapter<Event_source> dataAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remove_ids);

        exit = (Button) findViewById(R.id.add_remove_exit);
        add_manual = (Button)findViewById(R.id.add_remove_add_manual);
        manage_users = (Button) findViewById(R.id.add_remove_manage_users);
        submit_website = (Button) findViewById(R.id.add_remove_submit_url);
        url = (EditText) findViewById(R.id.add_remove_editText);
        proiv_icon = (ImageView) findViewById(R.id.add_remove_icon);

        spinner = (Spinner)findViewById(R.id.add_remove_id);
        by = (TextView) findViewById(R.id.add_remove_by);
        status = (TextView) findViewById(R.id.add_remove_status);
        delete = (Button) findViewById(R.id.add_remove_delete);
        event_url = (TextView) findViewById(R.id.add_remove_url);
        facebook_btn = (Button)findViewById(R.id.add_remove_facebook);
        google_btn = (Button) findViewById(R.id.add_remove_google);
        notification_button = (Button) findViewById(R.id.add_remove_notification);

        Typeface font = Typeface.createFromAsset(getAssets(), "ktavyad.otf");
        exit.setTypeface(font);
        add_manual.setTypeface(font);
        manage_users.setTypeface(font);
        submit_website.setTypeface(font);
        delete.setTypeface(font);
        facebook_btn.setTypeface(font);
        google_btn.setTypeface(font);
        notification_button.setTypeface(font);

        url.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        url.setFocusable(true);
        notification_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
                Intent intent = new Intent(add_remove_ids.this,choose_notification.class);
                startActivity(intent);
            }
        });

        create_meta_row(add_remove_ids.this);

        exit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                
                firebase_services.log_out();
                finish();
            }
        });
        add_manual.setText("הזן אירוע ידנית");
        add_manual.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
                Intent add_manual_event = new Intent(add_remove_ids.this, add_manual_event.class);
                startActivity(add_manual_event);
            }
        });

        manage_users.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
                Intent manage_users_intent = new Intent(add_remove_ids.this, manage_users.class);
                startActivity(manage_users_intent);
            }
        });

        submit_website.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(url.getText().toString().length() > 0)
                {
                    
                    String username = firebase_services.getuser().getEmail().substring(0,firebase_services.getuser().getEmail().indexOf('@'));
                    firebase_services.set_user_url(getBaseContext(),username,url.getText().toString());
                    Toast.makeText(add_remove_ids.this, "Set URL.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        new AsyncCaller().execute();
        new AsyncCaller_events().execute();
    }
    @Override
    public void onBackPressed()
    {
        exit.callOnClick();

    }
    public void update_url()
    {
        Vector<User> users = firebase_services.users;
        if(firebase_services.getuser() == null)
            return;
        String user = firebase_services.getuser().getEmail().substring(0,firebase_services.getuser().getEmail().indexOf('@'));
        if(admin.equals(user))
        {
            manage_users.setVisibility(View.VISIBLE);
        }
        for(int i=0; i<users.size(); i++)
        {
            if(user.equals(users.elementAt(i).id) && users.elementAt(i).url != null)
            {
                url.setText(users.elementAt(i).url);
            }
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent in)
    {
        //super(requestCode,resultCode,in);
        final String me = firebase_services.getuser().getEmail().substring(0,firebase_services.getuser().getEmail().indexOf('@'));
        final Vector<Event_source> event_sources = firebase_services.event_sources;
        dataAdapter.clear();
        Vector<User> server_users = firebase_services.users;
        for(Event_source src: event_sources)
        {
            if(me.equals(admin) || src.user.id.equals(me))
                dataAdapter.add(src);
        }
        dataAdapter.notifyDataSetChanged();
    }


    public void update_table()
    {
        final String me = firebase_services.getuser().getEmail().substring(0,firebase_services.getuser().getEmail().indexOf('@'));
        final Vector<Event_source> event_sources = firebase_services.event_sources;
        ArrayList<Event_source> event_source_list = new ArrayList<Event_source>();
        for(Event_source src: event_sources)
        {
            if(me.equals(admin) || src.user.id.equals(me))
                 event_source_list.add(src);
        }
        dataAdapter = new ArrayAdapter<Event_source>(this,
                simple_spinner_item, event_source_list);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id)
            {
                add_to_table(dataAdapter.getItem(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {
                delete.setVisibility(View.INVISIBLE);
                proiv_icon.setVisibility(View.INVISIBLE);
                by.setText("");
                status.setText("");
                event_url.setText("");


            }
        });
        /*
        for(int i=0; i<event_sources.size(); i++)
        {
            if(event_sources.elementAt(i).user.id.equals(user) || user.equals(admin))
            {
                add_to_table(event_sources.elementAt(i).user,event_sources.elementAt(i).provider_name,event_sources.elementAt(i).id);
            }
        }
        */
    }
    public void create_meta_row(final Context con)
    {
        if(firebase_services.getuser() == null)
            return;
        final String user = firebase_services.getuser().getEmail().substring(0,firebase_services.getuser().getEmail().indexOf('@'));

        /*
        Button add_facebook = new Button(this);
        add_facebook.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        Drawable facebook_image = this.getResources().getDrawable(R.mipmap.facebook);
        facebook_image.setBounds(0,0,45,75);
        add_facebook.setCompoundDrawables(facebook_image,null,null,null);
        */

        facebook_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                
                AlertDialog.Builder alert = new AlertDialog.Builder(con);
                final EditText edittext = new EditText(con);
                alert.setMessage("Please enter Facebook page ID here");
                alert.setTitle("Input data");

                alert.setView(edittext);

                alert.setPositiveButton("Yes Option", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        if(edittext.getText().toString().length() != 0)
                        {
                            firebase_services.add_event(con, "Facebook", user,edittext.getText().toString());
                            User selected_user = null;
                            for(int i=0; i<firebase_services.users.size() ; i++)
                            {
                                if(firebase_services.users.elementAt(i).id.equals(user))
                                    selected_user = firebase_services.users.elementAt(i);
                            }

                            for(User user : firebase_services.users)
                            {
                                Log.d("test",user.id + "," + selected_user.id);
                                if(user.id.equals(selected_user.id))
                                {
                                    Log.d("test","line 1");
                                    Event_source src = new Event_source("Facebook",edittext.getText().toString(),user);
                                    if(dataAdapter==null) {
                                     //   Log.d("spinner","fail");
                                        return;
                                    }
                                    dataAdapter.add(src);
                                    dataAdapter.notifyDataSetChanged();
                                    /*
                                    final String me = firebase_services.getuser().getEmail().substring(0,firebase_services.getuser().getEmail().indexOf('@'));
                                    final Vector<Event_source> event_sources = firebase_services.event_sources;
                                    ArrayList<Event_source> event_source_list = new ArrayList<Event_source>();
                                    for(Event_source srcs: event_sources)
                                    {
                                        if(me.equals(admin) || srcs.user.id.equals(me))
                                            event_source_list.add(srcs);
                                    }
                                    Log.d("spinner","yes");
                                    dataAdapter.clear();
                                    dataAdapter.addAll(event_source_list);
                                    spinner.setAdapter(dataAdapter);
                                    */
                                }
                            }

                        }
                    }
                });

                alert.setNegativeButton("No Option", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        // what ever you want to do with No option.
                    }
                });

                alert.show();
            }
        });

        /*
        Button add_google = new Button(this);
        add_google.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        Drawable google_image = this.getResources().getDrawable(R.mipmap.google_icon);
        google_image.setBounds(0,0,45,75);
        add_google.setCompoundDrawables(google_image,null,null,null);
*/

        google_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                
                AlertDialog.Builder alert = new AlertDialog.Builder(con);
                final EditText edittext = new EditText(con);
                alert.setMessage("Please enter Google calendar ID here");
                alert.setTitle("Input data");

                alert.setView(edittext);

                alert.setPositiveButton("Yes Option", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        if(edittext.getText().toString().length() != 0)
                        {
                            firebase_services.add_event(con, "Google", user,edittext.getText().toString());
                            User selected_user = null;
                            for(int i=0; i<firebase_services.users.size() ; i++)
                            {
                                if(firebase_services.users.elementAt(i).id.equals(user))
                                    selected_user = firebase_services.users.elementAt(i);
                            }
                            for(User user : firebase_services.users)
                            {
                                if(user.id.equals(selected_user.id))
                                {
                                    Event_source src = new Event_source("Google",edittext.getText().toString(),user);
                                    if(dataAdapter==null)
                                        return;
                                    dataAdapter.add(src);
                                    dataAdapter.notifyDataSetChanged();
                                    /*
                                    final String me = firebase_services.getuser().getEmail().substring(0,firebase_services.getuser().getEmail().indexOf('@'));
                                    final Vector<Event_source> event_sources = firebase_services.event_sources;
                                    ArrayList<Event_source> event_source_list = new ArrayList<Event_source>();
                                    for(Event_source srcs: event_sources)
                                    {
                                        if(me.equals(admin) || srcs.user.id.equals(me))
                                            event_source_list.add(srcs);
                                    }
                                    Log.d("spinner","yes");
                                    dataAdapter.clear();
                                    dataAdapter.addAll(event_source_list);
                                    spinner.setAdapter(dataAdapter);
                                    */
                                }
                            }
                        }
                    }
                });

                alert.setNegativeButton("No Option", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        // what ever you want to do with No option.
                    }
                });

                alert.show();
            }
        });


    }


    public void add_to_table(final Event_source src)
    {

        String URL = new String();
        proiv_icon.setVisibility(View.VISIBLE);
        if(src.provider_name.equals("Facebook"))
        {
            proiv_icon.setImageResource(R.drawable.com_facebook_button_icon_blue);
            URL+="https://www.facebook.com/";
        }
        else if(src.provider_name.equals("Google"))
        {
            proiv_icon.setImageResource(R.drawable.googleg_standard_color_18);
            URL+="https://calendar.google.com/calendar/embed?src=";
        }
        event_url.setText(URL);
        final String finalURL = URL;
        event_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalURL));
                startActivity(browserIntent);
            }
        });
        proiv_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalURL));
                startActivity(browserIntent);
            }
        });

        status.setText("Status: ");
        if(src.user.a_aprove) {
            status.append("approved ");
            status.setTextColor(Color.GREEN);
        }
        else {
            status.append("Pending  ");
            status.setTextColor(Color.parseColor("#ffa500"));
        }
        by.setText("By: " + src.user.id + " ");

        delete.setVisibility(View.VISIBLE);

        delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                
                firebase_services.remove_event_id(getBaseContext(),src.provider_name,src.user.id,src.id);
                dataAdapter.remove(src);
                if(dataAdapter.isEmpty())
                    delete.setVisibility(View.INVISIBLE);
                dataAdapter.notifyDataSetChanged();
            }
        });
    }

    public class AsyncCaller extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog pdLoading = new ProgressDialog(add_remove_ids.this);

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
            admin = getIntent().getStringExtra("admin");
            final String finalAdmin = admin;
            runOnUiThread(
            new Runnable()
            {
                @Override
                public void run()
                {
                    String user = firebase_services.getuser().getEmail().substring(0,firebase_services.getuser().getEmail().indexOf('@'));
                    if(admin.equals(user))
                    {
                        RelativeLayout layout = (RelativeLayout)findViewById(R.id.add_remove_layout);
                        layout.setBackgroundResource(R.mipmap.whitewood);
                        notification_button.setVisibility(View.VISIBLE);
                    }
                    update_url();
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

    public class AsyncCaller_events extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog pdLoading = new ProgressDialog(add_remove_ids.this);

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
            //this method will be running on background thread so don't update UI frome here
            //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here
            final firebase_services srv = new firebase_services();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    srv.SyncProvidors(add_remove_ids.this,getIntent(), firebase_services.getuser().getEmail().substring(0,firebase_services.getuser().getEmail().indexOf('@')),admin);
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
            firebase_services.Sync_providor(add_remove_ids.this,"Facebook");
            firebase_services.Sync_providor(add_remove_ids.this,"Google");
            while( getIntent().getStringExtra("Facebook")==null || getIntent().getStringExtra("Google")==null)
            {
                try
                {
                    Thread.sleep(1000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            runOnUiThread(
                    new Runnable()
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
