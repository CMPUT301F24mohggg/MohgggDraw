package com.example.mohgggdraw;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class WaitinglistDB {
    private FirebaseFirestore db;
    private CollectionReference waitlistRef;
    private String name;

    public WaitinglistDB(String name) {
        this.name = name;
        db = FirebaseFirestore.getInstance();
         waitlistRef= db.collection(name);
    }

    public CollectionReference getCitiesRef() {
        return waitlistRef;
    }

    public void setCitiesRef(CollectionReference citiesRef) {
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
        HashMap<String, String> data = new HashMap<>();
        data.put("email", user.getEmail());
        waitlistRef.document(user.getEmail()).set(data);




    }
}
