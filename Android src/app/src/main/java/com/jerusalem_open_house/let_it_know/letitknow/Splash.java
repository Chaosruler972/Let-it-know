package com.jerusalem_open_house.let_it_know.letitknow;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class Splash extends AppCompatActivity implements Animation.AnimationListener
{
    protected Animation fadeIn;
    protected ImageView img1;
    protected ImageView img2;
    private final int SPLASH_DISPLAY_LENGTH = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        img1 = (ImageView)findViewById(R.id.splashscreen);
        img2 = (ImageView)findViewById(R.id.imageView8);
        fadeIn = AnimationUtils.loadAnimation(this,R.anim.fade_in);
        img1.startAnimation(fadeIn);
        img2.startAnimation(fadeIn);
        Thread timerThread = new Thread(){
            public void run()
            {
                try
                {
                    sleep (2000);
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    Intent intent = new Intent(Splash.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();

    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation)
    {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
