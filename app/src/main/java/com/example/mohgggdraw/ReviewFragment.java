package com.example.mohgggdraw;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.provider.Settings;
import com.google.firebase.Timestamp;

/***
 This fragment provides a review of all entered event details. It:
 - Displays a summary of all event information
 - Allows the user to review and confirm the event details
 - Handles the final step of creating the event in Firebase Firestore
 ***/
public class ReviewFragment extends Fragment {
    private SharedViewModel sharedViewModel;
    private static final String TAG = "ReviewFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review, container, false);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Display selected data on the review page
        observeAndDisplaySelectedData(view);

        ImageView eventPoster = view.findViewById(R.id.image_event_banner);
        // Set up "Create Event" button to save data to Firebase and navigate to success page
        view.findViewById(R.id.button_create_event).setOnClickListener(v -> createEventInFirebase());

        // Load the image URL into the ImageView if available
        sharedViewModel.getImageUrl().observe(getViewLifecycleOwner(), url -> {
            if (url != null) {
                Glide.with(this).load(url).into(eventPoster);
            }
        });

        return view;
    }

    private void observeAndDisplaySelectedData(View view) {
        // Observing selected fields from SharedViewModel for the review page

        sharedViewModel.getEventTitle().observe(getViewLifecycleOwner(), title -> {
            TextView titleView = view.findViewById(R.id.text_event_title);
            if (title != null) titleView.setText(title);
        });

        sharedViewModel.getEventLocation().observe(getViewLifecycleOwner(), location -> {
            TextView locationView = view.findViewById(R.id.text_event_location);
            if (location != null) locationView.setText(location);
        });

        sharedViewModel.getRegistrationOpen().observe(getViewLifecycleOwner(), openDate -> {
            TextView openDateView = view.findViewById(R.id.text_event_open_date);
            if (openDate != null) openDateView.setText(openDate);
        });

        sharedViewModel.getRegistrationDeadline().observe(getViewLifecycleOwner(), deadline -> {
            TextView deadlineView = view.findViewById(R.id.text_event_close_date);
            if (deadline != null) deadlineView.setText(deadline);
        });

        sharedViewModel.getEventStartTime().observe(getViewLifecycleOwner(), startTime -> {
            TextView startTimeView = view.findViewById(R.id.text_event_start_date);
            if (startTime != null) startTimeView.setText(startTime);
        });

        // Max Pooling Sample
        sharedViewModel.getMaxPoolingSample().observe(getViewLifecycleOwner(), maxSample -> {
            TextView maxSampleTextView = view.findViewById(R.id.text_max_pooling_sample);
            if (maxSample != null) maxSampleTextView.setText(maxSample + " Pooling Sample");
        });

        // Max Entrants
        sharedViewModel.getMaxEntrants().observe(getViewLifecycleOwner(), maxEntrants -> {
            TextView maxEntrantsTextView = view.findViewById(R.id.text_max_entrants);
            if (maxEntrants != null) maxEntrantsTextView.setText(maxEntrants + " Entrants");
        });

        // Set the checkbox state based on geolocation status
        sharedViewModel.getEnableGeolocation().observe(getViewLifecycleOwner(), isEnabled -> {
            CheckBox geolocationCheckbox = view.findViewById(R.id.checkbox_enable_geolocation);
            geolocationCheckbox.setChecked(Boolean.TRUE.equals(isEnabled));
        });
    }

    void createEventInFirebase() {
        Toast.makeText(getContext(), "Creating Event...", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Create Event button clicked");

        // Retrieve device ID as organizer ID
        String deviceID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Reference to Firestore's "Events" collection
        CollectionReference eventsRef = FirebaseFirestore.getInstance().collection("Events");

        // Convert the dates to Firestore Timestamps
        Timestamp registrationOpenTimestamp = new Timestamp(new java.util.Date(sharedViewModel.getRegistrationOpen().getValue()));
        Timestamp registrationDeadlineTimestamp = new Timestamp(new java.util.Date(sharedViewModel.getRegistrationDeadline().getValue()));
        Timestamp eventStartTimeTimestamp = new Timestamp(new java.util.Date(sharedViewModel.getEventStartTime().getValue()));

        // Convert maxPoolingSample and maxEntrants to integers
        int maxPoolingSampleValue = 0;
        int maxEntrantsValue = 0;

        try {
            maxPoolingSampleValue = Integer.parseInt(sharedViewModel.getMaxPoolingSample().getValue());
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parsing maxPoolingSample, defaulting to 0", e);
        }

        try {
            maxEntrantsValue = Integer.parseInt(sharedViewModel.getMaxEntrants().getValue());
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parsing maxEntrants, defaulting to 0", e);
        }

        // Prepare all event data to save to Firestore
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("organizerId", deviceID);  // Use the device ID as organizer ID
        eventData.put("eventTitle", sharedViewModel.getEventTitle().getValue());
        eventData.put("eventLocation", sharedViewModel.getEventLocation().getValue());
        eventData.put("eventDetail", sharedViewModel.getEventDetail().getValue());
        eventData.put("registrationOpen", registrationOpenTimestamp);  // Use Timestamp
        eventData.put("registrationDeadline", registrationDeadlineTimestamp);  // Use Timestamp
        eventData.put("startTime", eventStartTimeTimestamp);  // Use Timestamp
        eventData.put("maxPoolingSample", maxPoolingSampleValue);
        eventData.put("maxEntrants", maxEntrantsValue);
        eventData.put("createDate", System.currentTimeMillis());
        eventData.put("geoLocationEnabled", sharedViewModel.getEnableGeolocation().getValue());
        eventData.put("imageUrl", sharedViewModel.getImageUrl().getValue());
        eventData.put("status", "active");
        eventData.put("QRhash", "generatedQRHash"); // Replace with actual QR code hash when available
        eventData.put("EventWaitinglist", new ArrayList<>());
        eventData.put("EventSelectedlist", new ArrayList<>());
        eventData.put("EventCancelledlist", new ArrayList<>());
        eventData.put("EventConfirmedlist", new ArrayList<>());

        // Add the new event data to Firestore
        eventsRef.add(eventData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Event successfully created and uploaded to Firestore!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to create event. Please try again.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error uploading event: ", e);
                });
    }
}
