package com.example.mohgggdraw;

public class WaitinglistController {


    private WaitinglistDB db;
    private Event event;

    public WaitinglistController(User user, Event event) {

        this.event = event;
        db = new WaitinglistDB(event.getName());
    }

    public void addUser(User user){
        db.addToDB(user);
        //implement another add if needed
        //will remove from wwaitlist arraylist when admin/organizer view as well

    }
}
