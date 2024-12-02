package com.example.mohgggdraw;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminEventDetails extends Fragment {

    private static final String ARG_EVENT_ID = "eventId";
    private static final String ARG_EVENT_TITLE = "eventTitle";
    private static final String ARG_EVENT_LOCATION = "eventLocation";
    private static final String ARG_EVENT_DATE = "eventDate";
    private static final String ARG_EVENT_TIME = "eventTime";
    private static final String ARG_EVENT_DETAILS = "eventDetails";
    private static final String ARG_EVENT_POSTER_URL = "eventPosterUrl";

    private String eventId;
    private String eventTitle;
    private String eventLocation;
    private String eventDate;
    private String eventTime;
    private String eventDetails;
    private String eventPosterUrl;

    public static AdminEventDetails newInstance(Event event) {
        AdminEventDetails fragment = new AdminEventDetails();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, event.getEventId());
        args.putString(ARG_EVENT_TITLE, event.getTitle());
        args.putString(ARG_EVENT_LOCATION, event.getLocation());
        args.putString(ARG_EVENT_DATE, event.getDate());
        args.putString(ARG_EVENT_TIME, event.getTime());
        args.putString(ARG_EVENT_DETAILS, event.getRegistrationDetails());
        args.putString(ARG_EVENT_POSTER_URL, event.getPosterUrl());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_event_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve arguments
        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);
            eventTitle = getArguments().getString(ARG_EVENT_TITLE, "No Title");
            eventLocation = getArguments().getString(ARG_EVENT_LOCATION, "No Location");
            eventDate = getArguments().getString(ARG_EVENT_DATE, "No Date");
            eventTime = getArguments().getString(ARG_EVENT_TIME, "No Time");
            eventDetails = getArguments().getString(ARG_EVENT_DETAILS, "No Details");
            eventPosterUrl = getArguments().getString(ARG_EVENT_POSTER_URL);
        }

        // Handle missing event data
        if (TextUtils.isEmpty(eventId)) {
            Toast.makeText(requireContext(), "Event ID is missing.", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
            return;
        }

        // Bind views
        ImageView eventPoster = view.findViewById(R.id.eventPoster);
        TextView eventTitleView = view.findViewById(R.id.eventTitle);
        TextView eventLocationView = view.findViewById(R.id.eventLocation);
        TextView eventDateView = view.findViewById(R.id.eventDate);
        TextView eventTimeView = view.findViewById(R.id.eventTime);
        TextView eventDetailsView = view.findViewById(R.id.eventRegistrationDetails);
        Button deleteEventButton = view.findViewById(R.id.delete_event_button);
        Button deleteQrHashButton = view.findViewById(R.id.delete_qr_code_button);

        // Set event details
        eventTitleView.setText(eventTitle);
        eventLocationView.setText(eventLocation);
        eventDateView.setText(eventDate);
        eventTimeView.setText(eventTime);
        eventDetailsView.setText(eventDetails);

        // Load event poster
        if (!TextUtils.isEmpty(eventPosterUrl)) {
            Glide.with(this)
                    .load(eventPosterUrl)
                    .placeholder(R.drawable.eventpage_banner_placeholder)
                    .into(eventPoster);
        } else {
            eventPoster.setImageResource(R.drawable.eventpage_banner_placeholder);
        }

        // Set delete event button action
        deleteEventButton.setOnClickListener(v -> showDeleteEventDialog());

        // Set delete QR code button action
        deleteQrHashButton.setOnClickListener(v -> showDeleteQrConfirmationDialog());
    }

    private void deleteEvent() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Events")
                .document(eventId)
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(requireContext(), "Event \"" + eventTitle + "\" deleted successfully.", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to delete event: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showDeleteEventDialog() {
        // Inflate the custom layout
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.custom_confirmation_dialog, null);

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
        Button confirmButton = dialogView.findViewById(R.id.confirm_button);
        TextView cancelTextView = dialogView.findViewById(R.id.cancel_text_view);
        TextView popUpTitle = dialogView.findViewById(R.id.title_text_view);
        TextView popUpMessage = dialogView.findViewById(R.id.message_text_view);

        // Set custom text for the dialog
        popUpTitle.setText("Delete Event?");
        popUpMessage.setText("Are you sure you want to delete this event for good?");

        // Handle confirm button click
        confirmButton.setOnClickListener(v -> {
            dialog.dismiss();
            deleteEvent(); // Trigger the deletion logic
        });

        // Handle "Nevermind" button click
        cancelTextView.setOnClickListener(v -> dialog.dismiss());

        // Show the dialog
        dialog.show();
    }

    private void showDeleteQrConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete QR Code")
                .setMessage("Are you sure you want to delete the QR code for this event?")
                .setPositiveButton("Delete", (dialog, which) -> deleteQrHash())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteQrHash() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Events")
                .document(eventId)
                .update("QRhash", "")
                .addOnSuccessListener(aVoid -> Toast.makeText(requireContext(), "QR Code deleted!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Unable to delete QR Code: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
