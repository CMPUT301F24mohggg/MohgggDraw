package com.example.mohgggdraw;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

/***
 * This fragment is shown after an event is successfully created. It:
 * - Displays a success message to the user
 * - Allows the user to update the event poster image
 * - Handles image selection and uploading to Firebase Storage
 ***/
public class EventEditPageFragment extends Fragment {
    private SharedViewModel sharedViewModel;
    private Uri newImageUri;

    private EditText eventTitleInput;
    private EditText eventLocationInput;
    private EditText eventDetailInput;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_edit, container, false);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        ImageView eventPoster = view.findViewById(R.id.organizer_event_poster);
        eventTitleInput = view.findViewById(R.id.event_title_input);
        eventLocationInput = view.findViewById(R.id.event_location_input);
        eventDetailInput = view.findViewById(R.id.event_detail_input);

        Button saveButton = view.findViewById(R.id.save_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        // Load initial event poster image from ViewModel
        sharedViewModel.getImageUrl().observe(getViewLifecycleOwner(), url -> {
            if (url != null) {
                Glide.with(this).load(url).into(eventPoster);
            }
        });

        // Load event details from ViewModel
        sharedViewModel.getEventTitle().observe(getViewLifecycleOwner(), title -> {
            if (title != null) {
                eventTitleInput.setText(title);
            }
        });

        sharedViewModel.getEventLocation().observe(getViewLifecycleOwner(), location -> {
            if (location != null) {
                eventLocationInput.setText(location);
            }
        });

        sharedViewModel.getEventDetail().observe(getViewLifecycleOwner(), detail -> {
            if (detail != null) {
                eventDetailInput.setText(detail);
            }
        });

        // Click listener for updating the event poster
        eventPoster.setOnClickListener(v -> selectNewImage());

        // Click listener for saving changes
        saveButton.setOnClickListener(v -> saveEventChanges());

        // Click listener for canceling edits
        cancelButton.setOnClickListener(v -> requireActivity().onBackPressed());

        return view;
    }

    private void selectNewImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 200);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && data != null) {
            newImageUri = data.getData();
            if (newImageUri != null) uploadNewImage(newImageUri);
        }
    }

    private void uploadNewImage(Uri filePath) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference ref = storage.getReference().child("event_images/" + UUID.randomUUID().toString());

        ref.putFile(filePath)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    String newImageUrl = uri.toString();
                    sharedViewModel.setImageUrl(newImageUrl);

                    // Update image URL in Firebase Realtime Database
                    String eventId = "yourEventId"; // Replace with the actual eventId for this event
                    FirebaseDatabase.getInstance().getReference("events")
                            .child(eventId)
                            .child("imageUrl")
                            .setValue(newImageUrl)
                            .addOnSuccessListener(aVoid -> {
                                // Successfully updated in Realtime Database
                            });

                    // Update image URL in Firestore
                    FirebaseFirestore.getInstance().collection("Events")
                            .document(eventId)
                            .update("imageUrl", newImageUrl)
                            .addOnSuccessListener(aVoid -> {
                                // Successfully updated in Firestore
                            })
                            .addOnFailureListener(e -> {
                                // Handle failure to update in Firestore
                            });

                }))
                .addOnFailureListener(e -> {
                    // Handle failure to upload or update image
                });
    }

    private void saveEventChanges() {
        String updatedTitle = eventTitleInput.getText().toString().trim();
        String updatedLocation = eventLocationInput.getText().toString().trim();
        String updatedDetail = eventDetailInput.getText().toString().trim();

        String eventId = "yourEventId"; // Replace with the actual eventId for this event

        // Update event details in Firebase Realtime Database
        FirebaseDatabase.getInstance().getReference("events")
                .child(eventId)
                .child("title")
                .setValue(updatedTitle);

        FirebaseDatabase.getInstance().getReference("events")
                .child(eventId)
                .child("location")
                .setValue(updatedLocation);

        FirebaseDatabase.getInstance().getReference("events")
                .child(eventId)
                .child("detail")
                .setValue(updatedDetail);

        // Update event details in Firestore
        FirebaseFirestore.getInstance().collection("Events")
                .document(eventId)
                .update("title", updatedTitle,
                        "location", updatedLocation,
                        "detail", updatedDetail)
                .addOnSuccessListener(aVoid -> {
                    // Successfully updated in Firestore
                })
                .addOnFailureListener(e -> {
                    // Handle failure to update in Firestore
                });

        // You can navigate back or show a success message here
    }
}
