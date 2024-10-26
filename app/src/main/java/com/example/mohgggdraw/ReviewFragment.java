package com.example.mohgggdraw;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReviewFragment extends Fragment {

    private OrganizerViewModel organizerViewModel;
    private FirebaseFirestore db;
    private StorageReference storageReference;

    private static final String TAG = "ReviewFragment"; // For logging

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review, container, false);

        Button createEventButton = view.findViewById(R.id.create_event_button);

        // Initialize ViewModel, Firebase, and Storage
        organizerViewModel = new ViewModelProvider(requireActivity()).get(OrganizerViewModel.class);
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        createEventButton.setOnClickListener(v -> {
            logViewModelData(); // Log the saved data
            saveEventToFirebase();
        });

        return view;
    }

    private void logViewModelData() {
        Uri imageUri = organizerViewModel.getEventImageUri();
        String title = organizerViewModel.getEventTitle();
        String location = organizerViewModel.getEventLocation();
        String detail = organizerViewModel.getEventDetail();

        // Log each field
        Log.d(TAG, "Event Data:");
        Log.d(TAG, "Image URI: " + (imageUri != null ? imageUri.toString() : "No image selected"));
        Log.d(TAG, "Title: " + (title != null ? title : "No title provided"));
        Log.d(TAG, "Location: " + (location != null ? location : "No location provided"));
        Log.d(TAG, "Detail: " + (detail != null ? detail : "No details provided"));

        // Continue to log any additional data saved in the ViewModel as needed
    }

    private void saveEventToFirebase() {
        Uri imageUri = organizerViewModel.getEventImageUri();
        if (imageUri != null) {
            // Generate a unique name for the image
            String imageName = "images/" + UUID.randomUUID().toString();

            // Create a reference to Firebase Storage
            StorageReference imageRef = storageReference.child(imageName);

            // Upload the image to Firebase Storage
            UploadTask uploadTask = imageRef.putFile(imageUri);
            uploadTask.addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Get the download URL and save all details to Firestore
                String downloadUrl = uri.toString();

                // Create a document to store in Firestore under "events" collection
                Map<String, Object> event = new HashMap<>();
                event.put("eventPosterUrl", downloadUrl);
                event.put("title", organizerViewModel.getEventTitle());
                event.put("location", organizerViewModel.getEventLocation());
                event.put("detail", organizerViewModel.getEventDetail());

                // Add to the "events" collection (replace 'eventId' with actual unique event identifier)
                db.collection("events").document("eventId")
                        .set(event)
                        .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Event created successfully!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to create event.", Toast.LENGTH_SHORT).show());
            }));
        } else {
            // Handle case where image URI is null
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }
}
