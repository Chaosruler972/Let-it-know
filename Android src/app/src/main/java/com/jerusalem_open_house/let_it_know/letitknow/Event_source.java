package com.jerusalem_open_house.let_it_know.letitknow;

/**
 * Created by Chaosruler on 05-Jun-17.
 */

public class Event_source
{
    public String provider_name;
    public String id;
    public User user;
    public manual_event event;
    public Event_source(String provider_name, String id, User user)
    {
        this.provider_name = provider_name;
        this.id = id;
        this.user = user;
    }
    public String toString()
    {
        return "Event ID: " + id;
    }
}
