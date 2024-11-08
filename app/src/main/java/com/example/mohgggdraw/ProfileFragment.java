
package com.example.mohgggdraw;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private ImageView profileImageView;
    private TextView nameTextView;
    private StorageReference storageReference;
    private String deviceID;
    private String userName;
    private boolean isGalleryImage = false;

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText editTextName, editTextPhone, editTextEmail, editTextLocation;
    private Button buttonSubmit, buttonDelete, buttonUploadImage, buttonDeleteImage;
    private ImageView profileImageView;
    private FirebaseFirestore db;
    private String deviceID;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initViews(view);


        nameTextView.setOnClickListener(v -> promptUserName());
        profileImageView.setOnClickListener(v -> handleProfileImageClick());


        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        return view;

    }
    // initializing UI elements and retrieving device ID
    private void initViews(View view) {
        profileImageView = view.findViewById(R.id.profileImageView);
        nameTextView = view.findViewById(R.id.nameTextView);
        deviceID = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private void promptUserName() {
        EditText input = new EditText(getContext());
        new AlertDialog.Builder(getContext())
                .setTitle("Enter Your Name")
                .setView(input)
                .setPositiveButton("OK", (dialog, which) -> setUserName(input.getText().toString().trim()))
                .setNegativeButton("Cancel", null)
                .show();
    }
    // set user name and display default profile picture
    private void setUserName(String name) {
        userName = name.isEmpty() ? "User" : name;
        nameTextView.setText(userName);
        setDefaultProfilePicture();
        saveUserProfileToFirebase(null);
    }

    private void handleProfileImageClick() {
        if (isGalleryImage) {
            confirmDeleteProfileImage();
        } else {
            showImageOptions();
        }
    }

    private void showImageOptions() {
        new AlertDialog.Builder(getContext())
                .setTitle("Profile Picture")
                .setMessage("Would you like to upload a profile picture from the gallery?")
                .setPositiveButton("Yes", (dialog, which) -> openGallery())
                .setNegativeButton("No", (dialog, which) -> resetToDefaultProfilePicture())
                .show();
    }

    private void confirmDeleteProfileImage() {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Profile Picture")
                .setMessage("Do you want to delete the current profile picture?")
                .setPositiveButton("Yes", (dialog, which) -> resetToDefaultProfilePicture())
                .setNegativeButton("No", null)
                .show();
    }

    private void resetToDefaultProfilePicture() {
        setDefaultProfilePicture();
        isGalleryImage = false;
        saveUserProfileToFirebase(null);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void setDefaultProfilePicture() {
        if (userName != null && !userName.isEmpty()) {
            profileImageView.setImageDrawable(createInitialsDrawable(getInitials(userName)));
        }
    }

    private String getInitials(String name) {
        String[] parts = name.split(" ");
        return parts.length >= 2 ? parts[0].substring(0, 1) + parts[1].substring(0, 1) : parts[0].substring(0, 1);
    }
    // creating a drawable to create the default user picture
    private Drawable createInitialsDrawable(String initials) {
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setTextSize(50);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(initials, 50, 65, paint);
        return new BitmapDrawable(getResources(), bitmap);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            handleGalleryResult(data.getData());
        }
    }

    private void handleGalleryResult(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
            profileImageView.setImageBitmap(bitmap);
            isGalleryImage = true;
            uploadProfilePictureToFirebase(bitmap);
        } catch (IOException e) {
            Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
            Log.e("ProfileFragment", "Error loading image", e);
        }
    }

    private void uploadProfilePictureToFirebase(Bitmap bitmap) {
        byte[] data = getImageData(bitmap);
        StorageReference profileRef = storageReference.child("profile_pictures/" + deviceID + ".jpg");

        profileRef.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> profileRef.getDownloadUrl().addOnSuccessListener(uri -> saveUserProfileToFirebase(uri.toString())))
                .addOnFailureListener(e -> Log.e("ProfileFragment", "Error uploading profile picture", e));
    }
    // converting bitmap to byte array before uploading
    private byte[] getImageData(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    private void saveUserProfileToFirebase(String imageUrl) {
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("deviceID", deviceID);
        userProfile.put("userName", userName);
        if (imageUrl != null) userProfile.put("profileImageUrl", imageUrl);

        db.collection("user").document(deviceID)
                .set(userProfile)
                .addOnSuccessListener(aVoid -> Log.d("ProfileFragment", "User profile saved successfully"))
                .addOnFailureListener(e -> Log.e("ProfileFragment", "Error saving user profile", e));
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

