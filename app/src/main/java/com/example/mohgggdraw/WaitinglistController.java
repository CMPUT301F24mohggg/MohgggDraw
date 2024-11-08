package com.example.mohgggdraw;

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
    //remove from db and update event
    public void removeUser(User user){
        db.removeFromDB(user,event);
        event.removeFromWaitingList(user);

    }

}
