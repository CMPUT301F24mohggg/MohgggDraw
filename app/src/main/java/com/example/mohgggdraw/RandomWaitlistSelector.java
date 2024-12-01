package com.example.mohgggdraw;

import android.util.Log;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class RandomWaitlistSelector {
    WaitinglistController controller;
    Event event;
    Random rand;
    ArrayList<String> selected;
    ArrayList<String> accepted;


    public RandomWaitlistSelector(Event event) {

        this.event = event;
        controller = new WaitinglistController(event);
        rand = new Random();

    }

    public ArrayList<String> pickFromWaitlist(){
        //roll function
        //collect event waitinglist
        //roll into
        //selected should be null check when called
        ArrayList<String> selected;
        ArrayList<String> cancelled;

        selected = new ArrayList<String>();

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

    public void fillSelected(WaitlistViewEntrantsFragment frag){

       new WaitinglistDB().getOtherLists(event,this,frag);

    }

    public ArrayList<String> pickRemaining(Map<String,ArrayList<String>> data, WaitlistViewEntrantsFragment frag){
        //how to reroll
        //selected accepted cancelled
        //selected + accepted = total taken
        //total spots - total taken = amount to reroll


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
        Log.d("tag",selected.toString());
        frag.updateFragments();

        return selected;
    }
}
