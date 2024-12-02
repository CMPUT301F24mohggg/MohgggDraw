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
 * Handles interactions with the Firestore database to retrieve event-related data.
 */
public class ScannerDB {
    private FirebaseFirestore db;
    private CollectionReference eventRef;
    private DocumentSnapshot documentSnapshot;

    /**
     * Initializes the database and collection reference for events.
     */
    public ScannerDB() {
        db = FirebaseFirestore.getInstance();
        eventRef = db.collection("Events");
        documentSnapshot = null;
    }

    /**
     * Retrieves a DocumentSnapshot for a specific event ID from Firestore.
     *
     * @param eventId The unique identifier for the event.
     */
    private void getDocSnap(String eventId) {
        DocumentReference docRef = eventRef.document(eventId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            /**
             * Callback method when the document retrieval is complete.
             *
             * @param task The Firestore task containing the query result.
             */
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

    /**
     * Returns the DocumentSnapshot for a given event ID.
     *
     * @param eventId The unique identifier for the event.
     * @return The retrieved DocumentSnapshot, or null if not found.
     */
    public DocumentSnapshot returnDocSnap(String eventId) {
        getDocSnap(eventId);
        return documentSnapshot;
    }
}