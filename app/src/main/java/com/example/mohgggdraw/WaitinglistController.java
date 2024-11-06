package com.example.mohgggdraw;

public class WaitinglistController {


    private WaitinglistDB db;
    private Event event;

    public WaitinglistController(User user, Event event) {

        this.event = event;
        db = new WaitinglistDB(event);
    }

    public void addUser(User user){
        db.addToDB(user);
        event.addToWaitingList(user);
        //update
        //implement another add if needed
        //will remove from wwaitlist arraylist when admin/organizer view as well

    }
    public void removeUser(User user){
        db.removeFromDB(user);
        event.removeFromWaitingList(user);

    }

}
