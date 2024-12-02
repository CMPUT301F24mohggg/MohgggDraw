package com.example.mohgggdraw;

import android.app.AlertDialog;
import android.health.connect.datatypes.Device;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

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

            // Show confirmation dialog
            showConfirmationDialog(title, details);
        });

        return view;
    }

    private void showConfirmationDialog(String title, String details) {
        // Inflate the custom layout
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.join_event, null);

        // Build the AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();

        // Ensure rounded background and proper dimensions
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Get references to dialog buttons and texts
        Button acceptButton = dialogView.findViewById(R.id.accept_terms_button);
        TextView nevermindTextView = dialogView.findViewById(R.id.nevermindTextView);
        TextView popUpTitle = dialogView.findViewById(R.id.titleTextView);
        TextView popUpMessage = dialogView.findViewById(R.id.messageTextView);

        popUpTitle.setText("Notify Entrant?");
        popUpMessage.setText("Are you sure want to send notification to your chosen entrants?");

        // Handle accept button click
        acceptButton.setOnClickListener(v -> {
            dialog.dismiss();
            sendNotifications(title, details);
        });

        // Handle "Nevermind" button click
        nevermindTextView.setOnClickListener(v -> dialog.dismiss());

        // Show the dialog
        dialog.show();
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
                                NotificationUtils.sendNotification(title, details, deviceIds, eventId, null);
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

        // Clear UI fields after sending notifications
        clearFields();
        // Navigate back to the previous page
        if (home != null) {
            requireActivity().runOnUiThread(() -> {
                home.getViewPager2().setCurrentItem(1, false);
            });
        }
    }
    private void clearFields() {
        requireActivity().runOnUiThread(() -> {
            if (getView() != null) {
                EditText titleEditText = getView().findViewById(R.id.notification_title);
                EditText detailsEditText = getView().findViewById(R.id.notification_detail);

                titleEditText.setText("");
                detailsEditText.setText("");
            }
        });
    }



}