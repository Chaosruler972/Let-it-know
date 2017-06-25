package com.jerusalem_open_house.let_it_know.letitknow;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

import java.util.ArrayList;
import java.util.Vector;

import static android.R.layout.simple_spinner_item;

public class manage_users extends AppCompatActivity
{
    private Button create_user;
    private Button back;
    private Spinner spinner;
    private EditText name;
    private Button change_name;
    private Button sample;
    private Button change_color;
    private EditText password;
    private Button change_password;
    private Button delete;
    private CheckBox check;
    private ArrayAdapter<User> adapter;
    private boolean rubick = true;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        create_user = (Button)findViewById(R.id.manage_user_create);
        back = (Button) findViewById(R.id.manage_users_back);

        spinner = (Spinner) findViewById(R.id.manage_users_spinner);
        name = (EditText) findViewById(R.id.manage_users_name);
        change_name = (Button) findViewById(R.id.manage_users_change_name);
        sample = (Button) findViewById(R.id.manage_users_sample);
        change_color = (Button)findViewById(R.id.manage_users_change_color);
        password = (EditText)findViewById(R.id.manage_users_password);
        change_password = (Button) findViewById(R.id.manage_users_change_pw);
        delete = (Button)findViewById(R.id.manage_users_delete);
        check = (CheckBox)findViewById(R.id.manage_users_check);
        Typeface font = Typeface.createFromAsset(getAssets(), "ktavyad.otf");
        create_user.setTypeface(font);
        back.setTypeface(font);
        change_color.setTypeface(font);
        change_password.setTypeface(font);
        change_name.setTypeface(font);
        delete.setTypeface(font);
        back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
                Intent intent = new Intent(manage_users.this,add_remove_ids.class);
                startActivity(intent);
            }
        });

        create_user.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
                Intent create_new_user = new Intent(manage_users.this,add_user.class);
                startActivity(create_new_user);
            }
        });

        firebase_services.SyncUsers(getBaseContext());
        new AsyncCaller().execute();
    }
    public void onBackPressed()
    {
        back.callOnClick();

    }
    public void onActivityResult(int requestCode, int resultCode, Intent in)
    {
        //super(requestCode,resultCode,in);
        adapter.clear();
        Vector<User> server_users = firebase_services.users;
        for(User user: server_users)
        {
            adapter.add(user);
        }
        adapter.notifyDataSetChanged();
    }

    public void update_table(final String admin)
    {
        Vector<User> server_users = firebase_services.users;
        ArrayList<User> user_list = new ArrayList<User>();
        for(User user: server_users)
        {
            user_list.add(user);
        }
        adapter = new ArrayAdapter<User>(this, simple_spinner_item, user_list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id)
            {
                change_password.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
                check.setVisibility(View.VISIBLE);
                change_color.setVisibility(View.VISIBLE);
                sample.setVisibility(View.VISIBLE);
                final User user = adapter.getItem(position);
                name.setText(user.name);
                change_name.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if(name.getText().toString().length() != 0 )
                        {
                            
                            user.name = name.getText().toString();
                            firebase_services.set_user_name(getBaseContext(),user);
                        }
                    }
                });
                sample.setBackgroundColor(Color.parseColor("#" + user.color));
                change_color.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        
                        final ColorPicker cp = new ColorPicker(manage_users.this);
                        cp.show();
                        Button okColor = (Button)cp.findViewById(R.id.okColorButton);
                        okColor.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                
                                String hexColor = String.format("%06X", (0xFFFFFF & cp.getColor()));
                                user.change_color(hexColor);
                                firebase_services.set_user_color(getBaseContext(),user);
                                sample.setBackgroundColor(Color.parseColor("#"+hexColor));
                            }
                        });
                    }
                });

                change_password.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        
                        if(password.getText().toString().length() >= 6)
                        {
                            firebase_services.user_operation(getBaseContext(), user, admin, "Change password", password.getText().toString());
                        }
                        else
                        {
                            Toast.makeText(manage_users.this, "Password is too short! minimum length is 6", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                if(user.id.equals(admin))
                    delete.setVisibility(View.INVISIBLE);
                delete.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        
                        firebase_services.user_operation(getBaseContext(), user, admin, "Delete", null);
                        adapter.remove(user);
                        if(adapter.isEmpty())
                            delete.setVisibility(View.INVISIBLE);
                        adapter.notifyDataSetChanged();
                    }
                });
                check.setChecked(user.a_aprove);
                check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                    {
                        user.change_approve(isChecked);
                        firebase_services.set_user_approve(getBaseContext(),user);
                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {
                change_password.setVisibility(View.INVISIBLE);
                delete.setVisibility(View.INVISIBLE);
                check.setVisibility(View.INVISIBLE);
                change_color.setVisibility(View.INVISIBLE);
                sample.setVisibility(View.INVISIBLE);
                password.setText("");
                name.setText("");
                check.setChecked(false);


            }
        });
    }

    public class AsyncCaller extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog pdLoading = new ProgressDialog(manage_users.this);

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
            String admin = "";
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
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    update_table(finalAdmin);

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
