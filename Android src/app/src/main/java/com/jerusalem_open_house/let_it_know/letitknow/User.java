package com.jerusalem_open_house.let_it_know.letitknow;

import java.io.Serializable;

/**
 * Created by Chaosruler on 05-Jun-17.
 */

public class User implements Serializable
{
    public String color;
    public String name;
    public String id;
    public String url;
    public boolean a_aprove;
    public String get_auto_approve()
    {
        if(this.a_aprove)
            return "yes";
        else
            return "no";
    }
    public void set_auto_approve(String auto_approve)
    {
        if(auto_approve.equals("yes"))
            this.a_aprove = true;
        else
            this.a_aprove = false;

    }
    public String toString()
    {
        return "ID: " + this.id;
    }

    public void copy(User other)
    {
        this.color = other.color;
        this.id = other.id;
        this.name = other.name;
        this.url = other.url;
        this.a_aprove = other.a_aprove;
    }
    public void change_color(String color)
    {
        this.color = color;
    }
    public void change_approve(boolean boo)
    {
        this.a_aprove = boo;
    }
}
