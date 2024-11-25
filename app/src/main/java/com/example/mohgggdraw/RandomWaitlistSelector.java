package com.example.mohgggdraw;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class RandomWaitlistSelector {
    WaitinglistController controller;
    Event event;
    Random rand;


    public RandomWaitlistSelector(Event event) {

        this.event = event;
        controller = new WaitinglistController(event);
        rand = new Random();
    }

    public ArrayList<String> pickFromWaitlist(){
        //roll function
        //collect event waitinglist
        //roll into
        ArrayList<String> selected;
        ArrayList<String> cancelled;
        Map<String, ArrayList<String>> data = new WaitinglistDB().getOtherLists(event);
        selected = data.get("Selected");
        if(selected == null){
            selected = new ArrayList<String>();
        }

        if(event.getMaxCapacity()!=-1 &&(event.getWaitingList().size()>event.getMaxCapacity())){

            for(int i=0; i<event.getMaxCapacity(); i++){
                int pos = rand.nextInt(event.getWaitingList().size());
                String id = event.getWaitingList().remove(pos);
                selected.add(id);
            }

        }
        else{
            selected = event.getWaitingList();
            event.setWaitingList(new ArrayList<String>());

        }
        //push selected to db
        controller.updateLists(selected,event.getWaitingList());
        //return for test functions
        return selected;
    }


    public void setSeed(long i){
        rand.setSeed(i);

    }

    public ArrayList<String> pickRemaining(){
        //how to reroll
        //selected accepted cancelled
        //selected + accepted = total taken
        //total spots - total taken = amount to reroll
        ArrayList<String> selected;
        ArrayList<String> accepted;

        Map<String, ArrayList<String>> data = new WaitinglistDB().getOtherLists(event);
        accepted = data.get("Accepted");
        selected = data.get("Selected");
        int totalCap = event.getMaxCapacity();
        int rerollAmnt = totalCap - selected.size()-accepted.size();
        if((event.getWaitingList().size()>rerollAmnt)) {
            for (int i = 0; i < rerollAmnt; i++) {
                int pos = rand.nextInt(event.getWaitingList().size());
                String id = event.getWaitingList().remove(pos);
                selected.add(id);
            }
        }else{
            selected.addAll(event.getWaitingList());
            event.setWaitingList(new ArrayList<String>());
        }
        controller.updateLists(selected,event.getWaitingList());
        return selected;
    }
}
