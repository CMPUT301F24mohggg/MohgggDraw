package com.example.mohgggdraw;


import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.view.View;

import java.io.Serializable;

public class Event implements Serializable {
    //Placeholder
    String name = "placeholder";
    String date = "Jan 1";
    String time = "1";
    String location = "placeholder";



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
