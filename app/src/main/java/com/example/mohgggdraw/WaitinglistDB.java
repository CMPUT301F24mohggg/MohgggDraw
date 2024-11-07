package com.example.mohgggdraw;

import static android.content.ContentValues.TAG;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

import kotlin.jvm.Synchronized;

public class WaitinglistDB {
    private FirebaseFirestore db;
    private CollectionReference waitlistRef;
    private String name;
    private DocumentReference myDoc;
    private Map<String,Object> docData;
    private Event event;

    public WaitinglistDB(Event event) {
        this.name = name;
        db = FirebaseFirestore.getInstance();
         waitlistRef= db.collection("Events");
         myDoc = waitlistRef.document((String.valueOf(event.getEventId())));
         this.event = event;
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

    public void removeFromDB(User user){
        myDoc.update("waitingList",FieldValue.arrayRemove(user.getEmail()));
    }
    public StorageReference getImage(String path){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference ref = storage.getReferenceFromUrl(path);

        return ref;
    }

    public void updateWaitlist(){
        boolean present = false;
        myDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override

            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Log.d(TAG,"do i get here");

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    docData =document.getData();
                    if(docData!= null){
                        ArrayList myWaitlist = (ArrayList) docData.get("EventWaitinglist");
                        event.setWaitingList(myWaitlist);

                    }



                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }
}
