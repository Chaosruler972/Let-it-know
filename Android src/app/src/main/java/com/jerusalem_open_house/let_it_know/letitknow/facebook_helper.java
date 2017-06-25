package com.jerusalem_open_house.let_it_know.letitknow;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;
import com.roomorama.caldroid.CaldroidFragment;

import org.apache.commons.lang3.time.DateUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import hirondelle.date4j.DateTime;

/**
 * Created by Chaosruler on 08-Jun-17.
 */

public class facebook_helper {
    public void get_events(final String page_id, final Vector<event_data> arr, final Intent intent, final User user, final CaldroidFragment caldroidFragment, final Context con) {
        String token_str = con.getResources().getString(R.string.facebook_app_id) + con.getResources().getString(R.string.fb_client_id);
        AccessToken token = new AccessToken(token_str, FacebookSdk.getApplicationId(), "test", null, null, null, null, null);
        final Calendar c1 = Calendar.getInstance();
        c1.add(Calendar.MONTH, -12);
        final Calendar c2 = Calendar.getInstance();
        c2.add(Calendar.MONTH, +12);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        final GraphRequest.Callback graphCallback = new GraphRequest.Callback(){
            @Override
            public void onCompleted(GraphResponse response)
            {
                if (response.getError() != null) {
                    return;
                }
                Log.d("test", "entered once");
                JSONArray data = null;
                try
                {
                    data = response.getJSONObject().getJSONArray("data");
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                if (data != null)
                {
                    add_event(data, arr, user, caldroidFragment,con);
                }
                GraphRequest nextRequest = response.getRequestForPagedResults(GraphResponse.PagingDirection.NEXT);
                if(nextRequest != null)
                {
                    nextRequest.setCallback(this);
                    nextRequest.executeAndWait();
                }

            }
        };

        new GraphRequest(
                token,
                "/" + page_id + "/events?" + "since=" + c1.getTimeInMillis() / 1000 + "&until=" + c2.getTimeInMillis() / 1000,
                null,
                HttpMethod.GET,
                graphCallback
        ).executeAsync();
    }


    public void add_event(JSONArray data_array1, Vector<event_data> arr, User user, CaldroidFragment caldroidFragment, Context con) {
        try {
            for (int i = 0; i < data_array1.length(); i++)
            {

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                JSONObject pubKey = data_array1.getJSONObject(i);
                String name = pubKey.getString("name");
                String start_str = pubKey.getString("start_time");
                String end_str = pubKey.getString("end_time");

                event_data data = new event_data();
                data.start = dateFormat.parse(start_str);
                data.end = dateFormat.parse(end_str);
                data.name = name;
                data.user = user;
                data.location = pubKey.getJSONObject("place").getJSONObject("location").getString("street") + "," + pubKey.getJSONObject("place").getJSONObject("location").getString("city") + "," + pubKey.getJSONObject("place").getJSONObject("location").getString("country");
                data.url = "https://www.facebook.com/events/" + pubKey.getString("id");
                ColorDrawable color = new ColorDrawable(con.getResources().getColor(R.color.get_day));
                int num_of_days = (int) TimeUnit.MILLISECONDS.toDays(Math.abs(data.start.getTime() - data.end.getTime()));
                java.util.Date backup = new java.util.Date(data.start.getTime());
                for(int j=0; j<=num_of_days; j++)
                {
                    if(!DateUtils.isSameDay(backup,new java.util.Date()))
                       caldroidFragment.setBackgroundDrawableForDate(color,backup);
                    backup.setTime(backup.getTime() + 86400000);
                }
                caldroidFragment.refreshView();
                if (!arr.contains(data))
                    arr.add(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }
}
