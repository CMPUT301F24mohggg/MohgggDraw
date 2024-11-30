package com.example.mohgggdraw;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.content.ContextWrapper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * QrCreatedFragment displays a generated QR code for an event and allows sharing or viewing event details.
 *
 * - Shows QR code in an ImageView.
 * - Navigates to WaitlistFragment to view event details.
 * - Shares QR code image via an intent.
 *
 * Constructor:
 * - QrCreatedFragment(EventQr eventQr): Initializes with event QR data.
 */

public class QrCreatedFragment extends Fragment {
    Context context;
    private WaitinglistDB waitinglistDB;
    private Button qrShareButton;
    private Button viewEventButton;
    private ImageView newQrImageView;
    private EventQr eventQr;
    private SharedViewModel sharedViewModel;
    private String eventId;

    public QrCreatedFragment() {
        this.eventQr = null;
        this.context = getActivity();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qrcode_created, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newQrImageView = view.findViewById(R.id.new_qrcode_iv);
        viewEventButton = view.findViewById(R.id.qrview_button);
        qrShareButton = view.findViewById(R.id.qrshare_button);
        waitinglistDB = new WaitinglistDB();
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Observe eventQr from SharedViewModel
        sharedViewModel.getEventQr().observe(getViewLifecycleOwner(), eventQr -> {
            if (eventQr != null) {
                setEventQr(eventQr); // Update local instance
                updateUI(eventQr); // Update the UI with eventQr
            }
        });
    }


    private void updateUI(EventQr eventQr) {
        // Set QR code bitmap
        newQrImageView.setImageBitmap(eventQr.getQrBitmap());

        // Set button actions
        viewEventButton.setOnClickListener(v -> {
            createEvent();
            // Swap to the QrWaitlistFragment
            swapFragment(5);
        });

        qrShareButton.setOnClickListener(v -> qrShare(eventQr.getQrBitmap()));
    }


    private void swapFragment(int postition) {
        ((CreateFragment) requireParentFragment()).swapToFragment(postition);
    }


    public void setEventQr(EventQr eventQr) {
        this.eventQr = eventQr;
        this.eventId = eventQr.getEventId();
    }

    private void qrShare(Bitmap qrBitmap) {

        String stringPath = MediaStore.Images.Media.insertImage(this.getActivity().getContentResolver(),
                qrBitmap, "Event QR Code", null);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(stringPath));
        intent.putExtra(Intent.EXTRA_TEXT, "Share this event!");

        startActivity(Intent.createChooser(intent, "Share Event QR Code"));
    }

    private void createEvent() {
        // Get Document snapshot, create and set Event to SharedViewModel
        if (eventId != null) {
            DocumentReference docRef = waitinglistDB.getWaitlistRef().document((String) eventId);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        // If exists return event
                        Event event = waitinglistDB.docSnapshotToEvent(document);
                        sharedViewModel.setEvent(event);   // Send Event obj
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }
    }


}