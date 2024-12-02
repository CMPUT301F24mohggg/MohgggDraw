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

    public ArrayList<String> pickFromWaitlist() {
        ArrayList<String> selected;

        selected = new ArrayList<>();

        if (event.getMaxCapacity() != -1 && (event.getWaitingList().size() > event.getMaxCapacity())) {
            for (int i = 0; i < event.getMaxCapacity(); i++) {
                int pos = rand.nextInt(event.getWaitingList().size());
                String id = event.getWaitingList().remove(pos);
                selected.add(id);
            }
        } else {
            selected = event.getWaitingList();
            event.setWaitingList(new ArrayList<>());
        }

        controller.updateLists(selected, event.getWaitingList());
        //todo notifications

        return selected;
    }

    public void setSeed(long i) {
        rand.setSeed(i);
    }

    public void fillSelected(WaitlistViewEntrantsFragment frag) {
        new WaitinglistDB().getOtherLists(event, this, frag);
    }

    public ArrayList<String> pickRemaining(Map<String, ArrayList<String>> data, WaitlistViewEntrantsFragment frag) {
        if (frag == null) {
            Log.e("RandomWaitlistSelector", "Fragment is null, cannot update fragments.");
            return new ArrayList<>(); // Return an empty list to avoid crashes
        }

        accepted = data.get("Accepted");
        selected = data.get("Selected");
        int totalCap = event.getMaxCapacity();
        int rerollAmnt = totalCap - selected.size() - accepted.size();

        if (event.getWaitingList().size() > rerollAmnt) {
            for (int i = 0; i < rerollAmnt; i++) {
                int pos = rand.nextInt(event.getWaitingList().size());
                String id = event.getWaitingList().remove(pos);
                selected.add(id);
            }
        } else {
            selected.addAll(event.getWaitingList());
            event.setWaitingList(new ArrayList<>());
        }

        controller.updateLists(selected, event.getWaitingList());
        Log.d("tag", selected.toString());

        // Call updateFragments on the provided fragment
        //todo notification
        frag.updateFragments();

        return selected;
    }
}
