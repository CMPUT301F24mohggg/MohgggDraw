package com.example.mohgggdraw;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class Event implements Serializable {
    //Placeholder
    String name = "placeholder";
    String date = "Jan 1";
    String time = "1:00 pm";
    String location = "yash apartment";
    String path = "image_2024-11-06_000509633.png";
    int id=1;
    int maxCapacity = 100;
    boolean geolocation = true;
    private ArrayList<String> waitingList = new ArrayList<>();

    public Event() {
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public int getId() {
        return id;
    }

    public boolean hasGeolocation(){
        return geolocation;
    }

    public ArrayList getWaitingList() {
        return waitingList;
    }

    public void setWaitingList(ArrayList waitingList) {
        this.waitingList = waitingList;
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

    public void addToWaitingList(User user){
        waitingList.add(user.getEmail());
    }

    public void removeFromWaitingList(User user){
        waitingList.remove(user.getEmail());
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
