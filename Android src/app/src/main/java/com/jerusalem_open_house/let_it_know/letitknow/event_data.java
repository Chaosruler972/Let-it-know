package com.jerusalem_open_house.let_it_know.letitknow;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Chaosruler on 08-Jun-17.
 */

public class event_data implements Parcelable
{
    public User user;
    public String name;
    public Date start;
    public Date end;
    public String url;
    public String location;
    public event_data()
    {

    }

    protected event_data(Parcel in) {
        user = (User) in.readSerializable();
        String[] arr = new String[3];
        in.readStringArray(arr);
        name = arr[0];
        url=arr[1];
        location = arr[2];
        List<Date> list = new ArrayList<Date>();
        in.readList(list,Date.class.getClassLoader());
        start = list.get(0);
        end = list.get(1);

    }

    public static final Creator<event_data> CREATOR = new Creator<event_data>() {
        @Override
        public event_data createFromParcel(Parcel in) {
            return new event_data(in);
        }

        @Override
        public event_data[] newArray(int size) {
            return new event_data[size];
        }
    };

    public String toString()
    {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(start);
        cal2.setTime(end);
        SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
        String date1 = format1.format(end);
        return name + date1;
      //  return name + " From: " + cal1.get(Calendar.HOUR_OF_DAY) + ":" + cal1.get(Calendar.MINUTE) + " End: " + date1 + " " + cal2.get(Calendar.HOUR_OF_DAY) + ":" + cal2.get(Calendar.MINUTE);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        Log.d("test",toString());
        dest.writeSerializable(user);
        dest.writeStringArray(new String[]{name,url,location});
       // dest.writeValue(start);
       // dest.writeValue(end);
        List<Date> list = new ArrayList<Date>();
        list.add(start);
        list.add(end);
       // dest.writeLongArray(new long[]{start.getTime(), end.getTime()});
        dest.writeList(list);

    }

    private void readFromParcel(Parcel in)
    {
        // Read object

    }
}
