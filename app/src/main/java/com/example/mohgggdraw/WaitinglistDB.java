package com.example.mohgggdraw;

import static android.content.ContentValues.TAG;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/***
 * Waitinglist db interacts with all firebase directly
 * deals with adding querying and stuff for other classes
 * ***/
public class WaitinglistDB {
    private FirebaseFirestore db;
    private CollectionReference waitlistRef;
    private String name;
    private DocumentReference myDoc;
    private Map<String,Object> docData;
    private Event event;

    public WaitinglistDB() {

        db = FirebaseFirestore.getInstance();
         waitlistRef= db.collection("Events");

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

    //updates event doc waitlist field with new user
    public void addToDB(User user,Event event){
        myDoc = waitlistRef.document((String.valueOf(event.getEventId())));
        myDoc.update("EventWaitinglist", FieldValue.arrayUnion(user.getUid()));


    }
// removes event doc waitlist user
    public void removeFromDB(User user, Event event){
        myDoc = waitlistRef.document((String.valueOf(event.getEventId())));
        myDoc.update("EventWaitinglist",FieldValue.arrayRemove(user.getUid()));
    }
    //gets image from path in firebase
    public StorageReference getImage(String path){
        if(path==null){
            path = "https://firebasestorage.googleapis.com/v0/b/mohgggdraw.appspot.com/o/event_images%2F1730963184849.jpg?alt=media&token=8c93f3c0-2e18-494a-95ec-a95b864ccdbd";
        }
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference ref = storage.getReferenceFromUrl(path);

        return ref;
    }

    //updates waitlist of event. gets doc snapshot and rebuilds waitinglist
    public void updateWaitlist(Event event){
        myDoc = waitlistRef.document((String.valueOf(event.getEventId())));

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
    //query to pull all events into arraylist with the field eventwaitinglist mainly for test purpose

    public ArrayList<Event> queryAllWithWaitingList(EventListDisplayFragment fragment){
        ArrayList<Event> myArray= new ArrayList<>();
        Task query= waitlistRef.orderBy("EventWaitinglist").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot doc: queryDocumentSnapshots) {

                    myArray.add(docSnapshotToEvent(doc));
                }
                fragment.dataChange();
            }
        });;

        return myArray;
    }

    //takes doc snapshot of event and turns into event object
    public Event docSnapshotToEvent(DocumentSnapshot doc){


        Map map = doc.getData();
        //string concat are because database is very incosistent, many null where there should not be
        Event myevent= new Event(doc.getId(),(String)map.get("eventTitle")+"please dont leave this stuff empty",(String)map.get("eventLocation")+"please dont leave this stuff empty",(String)map
                .get("imageUrl"), (String)map.get("eventDetail")+"please dont leave this stuff empty");
        if(map.get("geoLocationEnabled")!=null) {
            myevent.setGeolocation((boolean) map.get("geoLocationEnabled"));
        }
            myevent.setOrgID((String)map.get("organizerId"));
        if(map.get("EventWaitinglist")!= null) {
            myevent.setWaitingList((ArrayList<String>) map.get("EventWaitinglist"));
        }else{
            myevent.setWaitingList(new ArrayList<String>());
        }
        return myevent;

    }
}
