package com.example.mohgggdraw;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class WaitinglistDB {
    private FirebaseFirestore db;
    private CollectionReference waitlistRef;
    private String name;
    private DocumentReference myDoc;

    public WaitinglistDB(Event event) {
        this.name = name;
        db = FirebaseFirestore.getInstance();
         waitlistRef= db.collection("Event");
         myDoc = waitlistRef.document((String.valueOf(event.getId())));
    }

    public CollectionReference getWaitlistRef() {
        return waitlistRef;
    }

    public void setWaitlistRef(CollectionReference citiesRef) {
        this.waitlistRef = citiesRef;
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public void setDb(FirebaseFirestore db) {
        this.db = db;
    }

    public String getName() {
        return name;
    }

    public void addToDB(User user){
        myDoc.update("waitingList", FieldValue.arrayUnion(user.getEmail()));


    }
}
