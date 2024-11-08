package com.example.mohgggdraw;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class docIdToEvent {
    private String eventId;
    private DocumentReference docRef;
    private DocumentSnapshot docSnap;
    private Map map;
//    private DocumentSnapshot docSnap;


    public docIdToEvent(String eventId) {
        this.eventId = eventId;
        this.docRef = FirebaseFirestore.getInstance().collection("Events").document(eventId);
        this.docSnap = null;
        this.map = null;
    }

    public void getDocSnap() {
        this.docRef.get().addOnSuccessListener(docSnapshot -> {
            if (docSnapshot.exists()) {
                this.docSnap = docSnapshot;
                this
                return;

            } else {
                Log.d("Firestore", "No such document exists!");
            }
        }).addOnFailureListener(e -> {
            Log.e("Firestore", "Failed to retrieve document", e);
        });
        return;
    }
    }

    public Event createEvent() {

        docRef.get().addOnSuccessListener(docSnap -> {
            if (docSnap.exists()) {
                // Access a specific field with getString("something")
                Map map = docSnap.getData();
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

            } else {
                Log.d("Firestore", "No such document exists!");
            }
        }).addOnFailureListener(e -> {
            Log.e("Firestore", "Failed to retrieve document", e);
        });
        return null;
    }
}




//
//    FirebaseFirestore
//
//    Map map = doc.getData();
//
//    Event myevent= new Event(doc.getId(),(String)map.get("eventTitle")+"please dont leave this stuff empty",(String)map.get("eventLocation")+"please dont leave this stuff empty",
//            (String)map.get("imageUrl"), (String)map.get("eventDetail")+"please dont leave this stuff empty");
//    if(map.get("geoLocationEnabled")!=null) {
//        myevent.setGeolocation((boolean) map.get("geoLocationEnabled"));
//    }
//    myevent.setOrgID((String)map.get("organizerId"));
//    if(map.get("EventWaitinglist")!= null) {
//        myevent.setWaitingList((ArrayList<String>) map.get("EventWaitinglist"));
//    }else{
//        myevent.setWaitingList(new ArrayList<String>());
//    }
//    return myevent;
//
//}
//}
