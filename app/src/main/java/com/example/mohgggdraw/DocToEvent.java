package com.example.mohgggdraw;

import android.util.Log;

import com.google.firebase.Firebase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/***
 This class functions as a way to create an Event object using a document ID
 - If document cannot be found within the Firestore DB createEvent() will return null
 ***/

public class DocToEvent {
    private ScannerDB scannerDB;
    private DocumentSnapshot documentSnapshot;
    private String eventId;


    public DocToEvent(DocumentSnapshot documentSnapshot) {
        scannerDB = new ScannerDB();
        this.eventId = eventId;
        documentSnapshot = scannerDB.returnDocSnap(eventId);

    }


    // DEBUG
    public String documentTitle() {
        return documentSnapshot.getString("eventTitle") + "please dont leave this stuff empty";
    }

    public Event createEvent() {
        if (documentSnapshot == null) {
            return null;
        }

        Map map = documentSnapshot.getData();
        Event event;
        event = new Event(this.eventId,
                documentSnapshot.getString("eventTitle")+"please dont leave this stuff empty",
                documentSnapshot.getString("eventLocation")+"please dont leave this stuff empty",
                documentSnapshot.getString("imageUrl"),
                documentSnapshot.getString("eventDetail")+"please dont leave this stuff empty");
        if(documentSnapshot.get("geoLocationEnabled") != null) {
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