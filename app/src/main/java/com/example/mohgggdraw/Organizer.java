package com.example.mohgggdraw;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Organizer extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private ImageView organizerEventPoster;
    private StorageReference storageReference;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_basic_information);

        organizerEventPoster = findViewById(R.id.organizer_event_poster);

        // Initialize Firebase Storage and Firestore
        storageReference = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();

        // Make ImageView clickable to open the gallery
        organizerEventPoster.setOnClickListener(v -> {
            // Open Gallery to pick an image
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            if (selectedImage != null) {
                // Display the image in ImageView
                organizerEventPoster.setImageURI(selectedImage);

                // Generate a unique name for the image
                String imageName = "images/" + UUID.randomUUID().toString();

                // Create a reference to Firebase Storage
                StorageReference imageRef = storageReference.child(imageName);

                // Upload the image to Firebase Storage
                UploadTask uploadTask = imageRef.putFile(selectedImage);
                uploadTask.addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Get the download URL and store it in Firestore
                    String downloadUrl = uri.toString();

                    // Create a document to store in Firestore under "users"
                    Map<String, Object> user = new HashMap<>();
                    user.put("eventPosterUrl", downloadUrl);

                    // Add to the "users" collection (replace 'userId' with actual user identifier)
                    db.collection("users").document("userId")
                            .set(user)
                            .addOnSuccessListener(aVoid -> Toast.makeText(Organizer.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(Organizer.this, "Failed to upload image.", Toast.LENGTH_SHORT).show());
                }));
            }
        }
    }
}
