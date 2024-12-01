package com.example.mohgggdraw;

import android.health.connect.datatypes.Device;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

// TODO: Make the send notification to another file so that i can call it from here
public class NotificationDetailsFragment extends Fragment {
    private  List<String> selectedLists;
    private Event event;
    private HomeFragment home;

    public NotificationDetailsFragment(Event event, List<String> selectedLists, HomeFragment home) {
        this.event = event;
        this.selectedLists = selectedLists;
        this.home = home;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_details, container, false);

        EditText titleEditText = view.findViewById(R.id.notification_title);
        EditText detailsEditText = view.findViewById(R.id.notification_detail);

        Button sendButton = view.findViewById(R.id.send_notification_button);
        sendButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString();
            String details = detailsEditText.getText().toString();

            if (title.isEmpty() || details.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Send notifications to selected recipients
            sendNotifications(title, details);
        });

        return view;
    }

    private void sendNotifications(String title, String details) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String eventId = event.getEventId();

        for (String selectedList : selectedLists) {
            // Fetch data for the selected list
            db.collection("Events")
                    .document(eventId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Retrieve the array for the selected list
                            ArrayList<String> deviceIds = (ArrayList<String>) documentSnapshot.get(selectedList);

                            if (deviceIds != null) {
                                // Use NotificationUtils to send notifications
                                // TODO: Create a constructor to put all these in and feed that into sendNotification
                                NotificationUtils.sendNotification(title, details, deviceIds, eventId, null);

                                // Set the list in the Event object
                                switch (selectedList) {
                                    case "EventSelectedlist":
                                        event.setSelectedList(deviceIds);
                                        break;
                                    case "EventConfirmedlist":
                                        event.setConfirmedList(deviceIds);
                                        break;
                                    case "EventCancelledlist":
                                        event.setCancelledList(deviceIds);
                                        break;
                                    case "EventWaitinglist":
                                        event.setWaitingList(deviceIds);
                                        break;
                                    default:
                                        Log.w("sendNotifications", "Unknown list: " + selectedList);
                                }
                            } else {
                                Log.w("sendNotifications", "List " + selectedList + " is null or empty.");
                            }
                        } else {
                            Log.w("sendNotifications", "Event not found: " + eventId);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("sendNotifications", "Error fetching data for list: " + selectedList, e);
                    });
        }

        // Clear UI fields after sending notifications
        Toast.makeText(getContext(), "Notifications sent successfully", Toast.LENGTH_SHORT).show();
        // TODO: Clear the title and details fields and reset checkboxes
    }



}