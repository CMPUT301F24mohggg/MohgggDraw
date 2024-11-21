package com.example.mohgggdraw;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collector;

/***
 This fragment starts an Intent for QR scanner. It:
 - Uses the camera to scan a QR code
 - Displays the event details if the QR code is valid
 ***/
public class ScannerFragment extends Fragment {

    private String eventId;
//    private DocumentSnapshot documentSnapshot;
    private WaitinglistDB waitinglistDB = new WaitinglistDB();
    private CollectionReference eventRef = waitinglistDB.getWaitlistRef();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scanqr, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


// Start QR scanner Activity
        Intent intent = new Intent(getActivity(), ScannerActivity.class);
        ScannerActivityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> ScannerActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent resultIntent = result.getData();
                        eventId = resultIntent.getStringExtra("qrTextKey");

                        // Validate ID
                        validEventId(eventId);
                    }
                }
            });



    private void goToEvent() {

    }



    private void validEventId(String eventId) {

        DocumentReference docRef = eventRef.document(eventId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        // If exists return event
                        Event event = returnEvent(document);
                        swapToEventFrag(event);
                    } else {
                        displayInvalid();
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }


    private void swapToEventFrag(Event event) {
        // Swap to the event fragment

    }


    private Event returnEvent(DocumentSnapshot document) {
        Event event = waitinglistDB.docSnapshotToEvent(document);
        return event;
    }


    private void displayInvalid() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Invalid QR Code");
        builder.setMessage("Please scan a valid QR code. ");
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }
}
