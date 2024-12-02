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
        ArrayList<String> selected = new ArrayList<>();
        ArrayList<String> waitingList = event.getWaitingList(); // Copy of the waiting list
        ArrayList<String> losers = new ArrayList<>(); // To hold the IDs of losers

        if (event.getMaxCapacity() != -1 && waitingList.size() > event.getMaxCapacity()) {
            for (int i = 0; i < event.getMaxCapacity(); i++) {
                int pos = rand.nextInt(waitingList.size());
                String id = waitingList.remove(pos);
                selected.add(id);
            }
            losers.addAll(waitingList); // Remaining users in the waiting list are losers
        } else {
            selected = waitingList;
            event.setWaitingList(new ArrayList<>()); // Clear the waiting list
        }

        controller.updateLists(selected, event.getWaitingList());

        // Sending notifications
        String eventId = event.getEventId(); // Assuming your Event class has a getId() method
        String winnerTitle = "Congratulations!";
        String winnerMessage = "You have been selected for the event: " + event.getTitle();
        String loserTitle = "Better luck next time!";
        String loserMessage = "You were not selected for the event: " + event.getTitle();

        // Notify winners
        NotificationUtils.sendNotification(winnerTitle, winnerMessage, selected, eventId, "selected");

        // Notify losers
        if (!losers.isEmpty()) {
            NotificationUtils.sendNotification(loserTitle, loserMessage, losers, eventId, "not_selected");
        }

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

        ArrayList<String> waitingList = event.getWaitingList(); // Copy of the waiting list
        ArrayList<String> newSelected = new ArrayList<>(); // Newly selected users

        if (waitingList.size() > rerollAmnt) {
            for (int i = 0; i < rerollAmnt; i++) {
                int pos = rand.nextInt(waitingList.size());
                String id = waitingList.remove(pos);
                newSelected.add(id);
            }
        } else {
            newSelected.addAll(waitingList);
            event.setWaitingList(new ArrayList<>()); // Clear the waiting list
        }

        selected.addAll(newSelected);
        controller.updateLists(selected, event.getWaitingList());

        // Send notifications to winners and losers
        String eventId = event.getEventId(); // Assuming Event class has a getId() method
        String winnerTitle = "Congratulations on the re-roll!";
        String winnerMessage = "You have been selected for the event " + event.getTitle();

        // Notify winners
        NotificationUtils.sendNotification(winnerTitle, winnerMessage, newSelected, eventId, "selected");

        Log.d("tag", selected.toString());

        // Call updateFragments on the provided fragment
        frag.updateFragments();

        return selected;
    }
}
