package com.example.mohgggdraw;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

/***
 This class functions as a way to create an Event object using a document ID
 - If document cannot be found within the Firestore DB this.exists will stay as false
 ***/

public class DocToEvent {
    private String eventId;
    private DocumentReference docRef;
    private DocumentSnapshot docSnap;
    private Map map;
    private boolean exists;


    public DocToEvent(String eventId) {
        this.eventId = eventId;
        this.docRef = FirebaseFirestore.getInstance().collection("Events").document(eventId);
        this.docSnap = null;
        this.map = null;
        this.exists = false;
    }

    public boolean docExists () {
        return this.exists;
    }

    public void getDocSnap() {
        int successfulFind;
        this.docRef.get().addOnSuccessListener(docSnapshot -> {
            if (docSnapshot.exists()) {
                this.docSnap = docSnapshot;
                this.map = docSnap.getData();
                this.exists = true;
            } else {
                Log.d("Firestore", "No such document exists!");
            }
        }).addOnFailureListener(e -> {
            Log.e("Firestore", "Failed to retrieve document", e);
        });
    }

    public Event createEvent() {
        // Access a specific field with getString("something")
        Event event;
        event = new Event(this.eventId,
                docSnap.getString("eventTitle")+"please dont leave this stuff empty",
                docSnap.getString("eventLocation")+"please dont leave this stuff empty",
                docSnap.getString("imageUrl"),
                docSnap.getString("eventDetail")+"please dont leave this stuff empty");
        if(docSnap.get("geoLocationEnabled") != null) {
            event.setGeolocation((boolean) map.get("geoLocationEnabled"));
        }
        event.setOrgID((String)map.get("organizerId"));
        if (map.get("EventWaitingList") != null) {
            event.setWaitingList((ArrayList<String>) map.get("EventWaitingList"));
        } else {
            event.setWaitingList(new ArrayList<String>());
        }
        return event;
    }
}
