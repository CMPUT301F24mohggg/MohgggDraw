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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

/***
 This fragment starts an Intent for QR scanner. It:
 - Uses the camera to scan a QR code
 - Displays the event details if the QR code is valid
 ***/
public class ScannerCameraFragment extends Fragment {
    private ScannerViewModel scannerViewModel;
    private String eventId;
    private WaitinglistDB waitinglistDB = new WaitinglistDB();
    private CollectionReference eventRef = waitinglistDB.getWaitlistRef();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        scannerViewModel = new ViewModelProvider(requireActivity()).get(ScannerViewModel.class);
        return inflater.inflate(R.layout.fragment_scanner_camera, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (scannerViewModel.getScanStatus() == 1) {
            scannerViewModel.setScanStatus(0);
            startScanner();
        }
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


    private void validEventId(String eventId) {
        // Validate the event ID format: no spaces, only alphanumeric characters
        if (eventId == null || !eventId.matches("^[a-zA-Z0-9]+$")) {
            setToPage(1);
            return;
        }


        try {
            DocumentReference docRef = eventRef.document((String) eventId);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {
                            // If exists return event
                            Event event = returnEvent(document);
                            scannerViewModel.setSharedEvent(event);   // Send Event obj
                            setToPage(2);
                        } else {
                            setToPage(1);
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void setToPage(int position) {
        ((ScannerFragment) requireParentFragment()).swapToFragment(position);
    }


    private void startScanner() {
        // Start QR scanner Activity
        Intent intent = new Intent(getActivity(), ScannerActivity.class);
        ScannerActivityResultLauncher.launch(intent);
    }



    private Event returnEvent(DocumentSnapshot document) {
        Event event = waitinglistDB.docSnapshotToEvent(document);
        return event;
    }
}
