package com.example.mohgggdraw;


import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


/**
 * Database manager for scanner-related data.
 */
public class ScannerDB {
    private FirebaseFirestore db;
    private CollectionReference eventRef;
    private DocumentSnapshot documentSnapshot;


    public ScannerDB() {
        db = FirebaseFirestore.getInstance();
        eventRef = db.collection("Events");
        documentSnapshot = null;
    }

    private void getDocSnap(String eventId) {
        DocumentReference docRef = eventRef.document(eventId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // If exists return document
                        documentSnapshot = document;

                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }


    public DocumentSnapshot returnDocSnap(String eventId) {
        getDocSnap(eventId);
        return documentSnapshot;
    }
}

