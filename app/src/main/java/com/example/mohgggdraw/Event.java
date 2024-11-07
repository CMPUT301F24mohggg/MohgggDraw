package com.example.mohgggdraw;


import java.util.ArrayList;

public class Event {
    private String eventId;
    private String title;
    private String location;
    private String posterUrl;
    private String registrationDetails;
    private String participationSettings;
    private String date="nov 1";
    private String time = "3:00";
    private int maxCapacity=100;
    private boolean geolocation = true;
    private ArrayList<String> waitingList = new ArrayList<>();

    // Required empty constructor for Firebase
    public Event(String eventId, String value, String location, String posterUrl, String registrationDetails, String participationSettings) {
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
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public String getRegistrationDetails() { return registrationDetails; }
    public void setRegistrationDetails(String registrationDetails) { this.registrationDetails = registrationDetails; }

    public String getParticipationSettings() { return participationSettings; }
    public void setParticipationSettings(String participationSettings) { this.participationSettings = participationSettings; }

    public void addToWaitingList(User user) {
        this.waitingList.add(user.getEmail());
    }

    public void removeFromWaitingList(User user) {
        waitingList.remove(user.getEmail());
    }
}

//public class Event  {
//    //Placeholder
//    String name = "placeholder";
//    String date = "Jan 1";
//    String time = "1:00 pm";
//    String location = "yash apartment";
//    String path = "https://firebasestorage.googleapis.com/v0/b/mohgggdraw.appspot.com/o/event_images%2F1730963184849.jpg?alt=media&token=8c93f3c0-2e18-494a-95ec-a95b864ccdbd";
//
//
//    String id="olKgM5GAgkLRUqo97eVS";
//    int maxCapacity = 100;
//    boolean geolocation = true;
//    private ArrayList<String> waitingList = new ArrayList<>();
//
//    public Event() {
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public int getMaxCapacity() {
//        return maxCapacity;
//    }
//
//    public void setMaxCapacity(int maxCapacity) {
//        this.maxCapacity = maxCapacity;
//    }
//
//    public String getId() {
//        return id;
//    }
//
//    public boolean hasGeolocation(){
//        return geolocation;
//    }
//
//    public ArrayList getWaitingList() {
//        return waitingList;
//    }
//
//    public void setWaitingList(ArrayList waitingList) {
//        this.waitingList = waitingList;
//    }
//
//    public String getPath() {
//        return path;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public void addToWaitingList(User user){
//        waitingList.add(user.getEmail());
//    }
//
//    public void removeFromWaitingList(User user){
//        waitingList.remove(user.getEmail());
//    }
//
//    public String getDate() {
//        return date;
//    }
//
//    public void setDate(String date) {
//        this.date = date;
//    }
//
//    public String getTime() {
//        return time;
//    }
//
//    public void setTime(String time) {
//        this.time = time;
//    }
//
//    public String getLocation() {
//        return location;
//    }
//
//    public void setLocation(String location) {
//        this.location = location;
//    }
//
//    public void setGeolocation(boolean bool){
//        this.geolocation = bool;
//    }
//
//
//
//}
