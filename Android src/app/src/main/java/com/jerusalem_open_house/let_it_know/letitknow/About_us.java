package com.jerusalem_open_house.let_it_know.letitknow;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class About_us extends AppCompatActivity
{
    private Button back;
    private ImageView avi;
    private ImageView terem;
    private ImageView meitar;
    private ImageView zeev;
    private ImageView sagi;
    private final String avi_g = "https://github.com/avicohen89";
    private final String sagi_g = "https://github.com/yeseg11";
    private final String meitar_g = "https://github.com/meitarsh";
    private final String terem_g = "https://github.com/Oterem";
    private final String zeev_g = "https://github.com/Chaosruler972";
    private final String avi_i = "https://avatars0.githubusercontent.com/u/26030890?v=3&s=460";
    private final String sagi_i = "https://avatars3.githubusercontent.com/u/16443118?v=3&s=460";
    private final String meitar_i = "https://avatars1.githubusercontent.com/u/26038128?v=3&s=460";
    private final String terem_i = "https://avatars3.githubusercontent.com/u/26056415?v=3&s=460";
    private final String zeev_i = "https://avatars2.githubusercontent.com/u/25983708?v=3&s=460";
    private final String project_url = "https://github.com/yeseg11/Let-it-know";
    private TextView project_page;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        Typeface font = Typeface.createFromAsset(getAssets(), "ktavyad.otf");
        back = (Button) findViewById(R.id.about_us_back);
        back.setTypeface(font);
        sagi = (ImageView)findViewById(R.id.img_sagi);
        avi = (ImageView)findViewById(R.id.img_avi);
        terem = (ImageView)findViewById(R.id.img_terem);
        meitar = (ImageView)findViewById(R.id.img_meitar);
        zeev = (ImageView)findViewById(R.id.img_zeev);
        Picasso.with(getBaseContext()).load(terem_i).into(terem);
        Picasso.with(getBaseContext()).load(avi_i).into(avi);
        Picasso.with(getBaseContext()).load(sagi_i).into(sagi);
        Picasso.with(getBaseContext()).load(zeev_i).into(zeev);
        Picasso.with(getBaseContext()).load(meitar_i).into(meitar);
        project_page = (TextView)findViewById(R.id.textView19);
        project_page.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(project_url));
                startActivity(browserIntent);
            }
        });
        sagi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(sagi_g));
                startActivity(browserIntent);
            }
        });
        avi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(avi_g));
                startActivity(browserIntent);
            }
        });
        terem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(terem_g));
                startActivity(browserIntent);
            }
        });
        meitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(meitar_g));
                startActivity(browserIntent);
            }
        });
        zeev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(zeev_g));
                startActivity(browserIntent);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        new AsyncCaller().execute();
    }

    public class AsyncCaller extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog pdLoading = new ProgressDialog(About_us.this);

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setCancelable(false);
            pdLoading.setMessage("\tטוען...");
            pdLoading.show();
        }
        @Override
        protected Void doInBackground(Void... params)
        {

            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);

            //this method will be running on UI thread

            pdLoading.dismiss();
        }

    }
}
