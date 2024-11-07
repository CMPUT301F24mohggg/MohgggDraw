package com.example.mohgggdraw;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private EditText editTextName, editTextPhone, editTextEmail, editTextLocation;
    private Button buttonSubmit, buttonDelete, buttonUploadImage, buttonDeleteImage;
    private ImageView profileImageView;
    private FirebaseFirestore db;
    private String deviceID;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        editTextName = view.findViewById(R.id.editTextName);
        editTextPhone = view.findViewById(R.id.editTextPhone);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextLocation = view.findViewById(R.id.editTextLocation);
        buttonSubmit = view.findViewById(R.id.buttonSubmit);
        buttonDelete = view.findViewById(R.id.buttonDelete);
        buttonUploadImage = view.findViewById(R.id.buttonUploadImage);
        buttonDeleteImage = view.findViewById(R.id.buttonDeleteImage);
        profileImageView = view.findViewById(R.id.profileImageView);

        // Get device ID
        deviceID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Load existing user data
        loadUserData();

        // Set up the submit button listener
        buttonSubmit.setOnClickListener(v -> updateUserData());

        // Set up the delete button listener
        buttonDelete.setOnClickListener(v -> deleteUserData());

        // Set up the upload image button listener
        buttonUploadImage.setOnClickListener(v -> {
            // Placeholder action for uploading an image
            Toast.makeText(getContext(), "Upload Image clicked", Toast.LENGTH_SHORT).show();
        });

        // Set up the delete image button listener
        buttonDeleteImage.setOnClickListener(v -> {
            // Placeholder action for deleting an image
            Toast.makeText(getContext(), "Delete Image clicked", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserData() {
        DocumentReference docRef = db.collection("user").document(deviceID);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                editTextName.setText(documentSnapshot.getString("name"));
                editTextPhone.setText(documentSnapshot.getString("phoneNumber"));
                editTextEmail.setText(documentSnapshot.getString("email"));
                editTextLocation.setText(documentSnapshot.getString("location"));
            } else {
                Toast.makeText(getContext(), "No user data found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Failed to load user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("ProfileFragment", "Failed to load user data: " + e.getMessage());
        });
    }

    private void updateUserData() {
        String name = editTextName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || location.isEmpty()) {
            Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create user data map
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("name", name);
        userDetails.put("phoneNumber", phone);
        userDetails.put("email", email);
        userDetails.put("location", location);

        // Update user data in Firestore
        db.collection("user").document(deviceID).update(userDetails)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("ProfileFragment", "Failed to update profile: " + e.getMessage());
                });
    }

    private void deleteUserData() {
        db.collection("user").document(deviceID).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Profile deleted successfully", Toast.LENGTH_SHORT).show();
                    navigateToSignupScreen();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to delete profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("ProfileFragment", "Failed to delete profile: " + e.getMessage());
                });
    }

    private void navigateToSignupScreen() {
        Intent intent = new Intent(getContext(), SignupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
