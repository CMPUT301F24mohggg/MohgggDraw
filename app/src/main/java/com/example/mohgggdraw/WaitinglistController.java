package com.example.mohgggdraw;

import android.annotation.SuppressLint;

/***
 * Controller to interact with event object and database together
 * ***/
public class WaitinglistController {


    private WaitinglistDB db;
    private Event event;

    public WaitinglistController(Event event) {
        this.event = event;
        db = new WaitinglistDB();
    }

    //add user to db and update event
    @SuppressLint("RestrictedApi")
    public void addUser(User user){
        db.addToDB(user, event);
        event.addToWaitingList(new com.google.firebase.firestore.auth.User("asdf"));


    }
    //remove from db and update event
    @SuppressLint("RestrictedApi")
    public void removeUser(User user){
        db.removeFromDB(user,event);
        event.removeFromWaitingList(new com.google.firebase.firestore.auth.User("asdf"));

    }

}
