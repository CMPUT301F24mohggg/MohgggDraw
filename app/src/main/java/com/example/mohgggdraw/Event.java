package com.example.mohgggdraw;

/***
 This class represents an event in the application. It:
 - Stores event details such as title, location, poster URL, etc.
 - Provides getters and setters for all event properties
 - Includes a no-arg constructor for Firebase compatibility
 ***/

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class Event {
    private String eventId;
    private String title;
    private String location;
    private String posterUrl;
    private String registrationDetails;
    private String participationSettings;
    private String date = "nov 1";
    private String time = "3:00";
    private int maxCapacity = -1;
    private boolean geolocation = false;
    private ArrayList<String> waitingList = new ArrayList<>();
    private String orgID = "dasf";
    public Event(String eventId, String title, String location, String posterUrl, String registrationDetails, String participationSettings) {}
    // Required empty constructor for Firebase
    public Event(String eventId, String title, String location, String posterUrl, String registrationDetails) {
        this.eventId = eventId;
        this.title = title;
        this.location = location;
        this.posterUrl = posterUrl;
        this.registrationDetails = registrationDetails;

    }

    public String getOrgID() {
        return orgID;
    }

    public void setOrgID(String orgID) {
        this.orgID = orgID;
    }

    public Event(String eventId, String title, String location, String posterUrl, String registrationDetails, String participationSettings, String value, String s, String string, Boolean aBoolean) {
        this.eventId = eventId;
        this.title = title;
        this.location = location;
        this.posterUrl = posterUrl;
        this.registrationDetails = registrationDetails;
        this.participationSettings = participationSettings;


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


    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public boolean isGeolocation() {
        return geolocation;
    }

    public void setGeolocation(boolean geolocation) {
        this.geolocation = geolocation;
    }

    public ArrayList<String> getWaitingList() {
        return waitingList;
    }

    public void setWaitingList(ArrayList<String> waitingList) {
        this.waitingList = waitingList;
    }

    // Getters and Setters for Firebase to use
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getRegistrationDetails() {
        return registrationDetails;
    }

    public void setRegistrationDetails(String registrationDetails) {
        this.registrationDetails = registrationDetails;
    }

    public String getParticipationSettings() {
        return participationSettings;
    }

    public void setParticipationSettings(String participationSettings) {
        this.participationSettings = participationSettings;
    }

    public void addToWaitingList(User user) {
        this.waitingList.add(user.getUid());
    }

    public void removeFromWaitingList(User user) {
        waitingList.remove(user.getUid());
    }
}
