package com.jerusalem_open_house.let_it_know.letitknow;


import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.util.Vector;

/**
 * Created by Chaosruler on 05-Jun-17.
 */

public class SQL_DB extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "Users.db";
    public static final String USERS_ID = "id";
    public static final String USERS__NAME = "name";
    public static final String USERS_COLOR = "color";
    public static final String USERS_APPROVE = "auto_approve";
    public static final String USER_URL = "url";
    public SQL_DB(Context context)
    {
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table Users ("+USERS_ID+" text primary key, "+USERS__NAME+" text, "+USERS_COLOR+" text, "+USERS_APPROVE+" text " +"," + USERS_APPROVE  +"text"+"," + USER_URL + " text) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Users");
        onCreate(db);
    }

    public void create_table_doesnt_exists(Context con)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("create table IF NOT EXISTS Users ("+USERS_ID+" text primary key, "+USERS__NAME+" text, "+USERS_COLOR+" text, "+USERS_APPROVE+" text " +"," + USERS_APPROVE  +"text"+"," + USER_URL + " text) ");
    }
    public void dropDB(Context con)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DROP TABLE IF EXISTS Users");
    }
    public void createDB(Context con)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        onCreate(db);
    }
    public boolean check_db_exists(Context con)
    {
        File dbFile = con.getDatabasePath(DATABASE_NAME);
        return dbFile.exists();
    }

    public boolean check_user_exists(Context con, User user)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM Users WHERE " + USERS_ID + " = " + "'" + user.id+"'" ,null  );
        if(c.getCount() == 0)
            return false;
        return true;
    }

    public boolean change_user_check(Context con, User user, boolean check_status)
    {
        if(check_user_exists(con,user))
        {
            user.a_aprove = check_status;
            remove_user(con,user);
            add_user(con,user);
            return true;
        }
        return false;
    }

    public void add_user(Context con, User user)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("INSERT INTO Users "+"( " + USERS_ID + "," + USERS__NAME +"," + USERS_COLOR + "," + USERS_APPROVE + "," + USER_URL +") VALUES (" + "'" + user.id + "','" + user.name + "','" + user.color + "','" + user.a_aprove + "','" + user.url +"')");
    }

    public void remove_user(Context con, User user)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM Users WHERE " + USERS_ID + "='" + user.id +"'");
    }

    public Vector<User> get_users(Context con)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Vector<User> users = new Vector<>();
        Cursor c = db.rawQuery("SELECT * FROM Users",null);
        c.moveToFirst();
        while(!c.isAfterLast())
        {
            User user = new User();
            user.id = c.getString(c.getColumnIndex(USERS_ID));
            user.color = c.getString(c.getColumnIndex(USERS_COLOR));
            user.name = c.getString(c.getColumnIndex(USERS__NAME));
            if(c.getString(c.getColumnIndex(USERS_APPROVE)).equals("true"))
                user.a_aprove = true;
            else
                user.a_aprove = false;
            if(!c.getString(c.getColumnIndex(USER_URL)).equals("null"))
            {
                user.url = c.getString(c.getColumnIndex(USER_URL));
            }
            users.add(user);
            c.moveToNext();
        }
        return users;
    }

}
