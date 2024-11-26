package com.example.mohgggdraw;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class UserDB {
    private FirebaseFirestore db;
    private CollectionReference waitlistRef;
    private String name;
    private DocumentReference myDoc;
    private Map data;

    public UserDB() {
        db = FirebaseFirestore.getInstance();
        waitlistRef= db.collection("Users");
    }

    public Map getUserMapFromID(String id){

        myDoc = waitlistRef.document((String.valueOf(id)));
        Task<DocumentSnapshot> query = myDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                data = documentSnapshot.getData();

            }
        });
        return data;
    }
}
