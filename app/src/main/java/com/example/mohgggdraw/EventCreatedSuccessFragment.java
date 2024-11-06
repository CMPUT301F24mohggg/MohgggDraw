package com.example.mohgggdraw;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class EventCreatedSuccessFragment extends Fragment {
    private SharedViewModel sharedViewModel;
    private Uri newImageUri;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_created_success, container, false);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        ImageView eventPoster = view.findViewById(R.id.organizer_event_poster);

        // Load initial event poster image from ViewModel
        sharedViewModel.getImageUrl().observe(getViewLifecycleOwner(), url -> {
            if (url != null) {
                Glide.with(this).load(url).into(eventPoster);
            }
        });

        view.findViewById(R.id.update_poster_button).setOnClickListener(v -> selectNewImage());

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
}
