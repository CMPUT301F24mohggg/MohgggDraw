package com.example.mohgggdraw;

import java.util.ArrayList;

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
    public void addUser(User user){
        db.addToDB(user,event);
        event.addToWaitingList(user);


    }

    public void addToCancelled(User user){

    }

    public void addToSelected(User user){


    }
    public void addToConfirmed(User user){

    }

    public void addUser(User user, String location){
        db.addToDB(user,event);
        event.addToWaitingList(user);


    }
    //remove from db and update event
    public void removeUser(User user){
        db.removeFromDB(user,event);
        event.removeFromWaitingList(user);

    }
    //
    public void updateLists(ArrayList selected, ArrayList waitlist){
        db.updateLists(event,selected,waitlist);
        event.setWaitingList(waitlist);


    }

}
