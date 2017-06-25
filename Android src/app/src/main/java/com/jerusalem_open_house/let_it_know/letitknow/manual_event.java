package com.jerusalem_open_house.let_it_know.letitknow;

import java.util.Date;

/**
 * Created by Chaosruler on 06-Jun-17.
 */

public class manual_event
{
    public String name;
    public String location;

    public Date start;
    public Date end;
    public String url;
    public User user;
    public String id;
    public manual_event(final manual_event other)
    {
        this.name = other.name + "";
        this.location = other.location + "";
        this.start = other.start;
        this.end = other.end;
        this.url = other.url + "";
        this.user = other.user;
        this.id = other.id;
    }

    public manual_event(String name, String location, long start_date, String start_time, long end_date, String end_time, String url)
    {
        this.name = name;
        this.location = location;
        long start_long = start_date;
        long end_long = end_date;

        int s_h = Integer.parseInt(start_time.substring(0,start_time.indexOf(':')));
        int s_m = Integer.parseInt(start_time.substring(start_time.indexOf(':')+1,start_time.length()));

        int e_h = Integer.parseInt(end_time.substring(0,end_time.indexOf(':')));
        int e_m = Integer.parseInt(end_time.substring(end_time.indexOf(':')+1,end_time.length()));

        start_long += (s_h)*3600000 + (s_m)*60000;
        end_long += (e_h)*3600000 + (e_m)*60000;

        start = new Date(start_long);

        end = new Date(end_long);

        this.url = url;

    }
    public manual_event()
    {

    }
    public String toString()
    {
        return "Name: " + this.name;
      //  return "Name: "+ this.name + " Location: " + this.location + " Start: " + start.toString() + " End: " + end.toString() + " URL: "+ url.toString();
    }
}
