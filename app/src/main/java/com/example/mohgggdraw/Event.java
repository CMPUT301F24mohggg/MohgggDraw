package com.example.mohgggdraw;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

public class Event implements Serializable {
    //Placeholder
    String name = "placeholder";
    String date = "Jan 1";
    String time = "1";
    String location = "placeholder";
    String path = "https://firebasestorage.googleapis.com/v0/b/mohgggdraw.appspot.com/o/Untitled.png?alt=media&token=26c6ac6d-12af-423d-af51-a176ae50abb7";
    Bitmap bmp;
    int id=1;

    public int getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }



}
