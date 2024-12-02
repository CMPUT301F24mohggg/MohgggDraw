package com.example.mohgggdraw;

import android.app.AlertDialog;
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
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * ProfileFragment handles the user's profile management functionality.
 * <p>
 * Features include:
 * <ul>
 * <li>Viewing and updating profile information</li>
 * <li>Uploading, deleting, or resetting profile pictures</li>
 * <li>Deleting the user's profile</li>
 * </ul>
 */
public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView profileImageView;
    private TextView nameTextView;
    private EditText editTextName, editTextPhone, editTextEmail, editTextLocation;
    private Button buttonSubmit, buttonDelete;
    private Toolbar toolbar;

    private StorageReference storageReference;
    private FirebaseFirestore db;
    private String deviceID;
    private String userName;
    private boolean isGalleryImage = false;

    /**
     * Inflates the layout for the fragment and initializes UI elements.
     *
     * @param inflater           The LayoutInflater used to inflate the layout.
     * @param container          The parent view that this fragment's UI is attached to.
     * @param savedInstanceState The saved state of the fragment, if available.
     * @return The created View for the fragment.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views and Firebase
        initViews(view);
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        // Get device ID
        deviceID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Load user data
        loadUserData();

        // Set up listeners
        setupListeners();

        return view;
    }

    /**
     * Initializes the UI components for the profile screen.
     *
     * @param view The root view of the fragment.
     */
    private void initViews(View view) {
        profileImageView = view.findViewById(R.id.profileImageView);
        nameTextView = view.findViewById(R.id.nameTextView);
        editTextName = view.findViewById(R.id.editTextName);
        editTextPhone = view.findViewById(R.id.editTextPhone);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextLocation = view.findViewById(R.id.editTextLocation);
        buttonSubmit = view.findViewById(R.id.buttonSubmit);
        buttonDelete = view.findViewById(R.id.buttonDelete);
        toolbar = view.findViewById(R.id.toolbar);

        // Setup toolbar navigation
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
            }
        }

        toolbar.setNavigationOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
    }

    /**
     * Sets up listeners for profile image actions, updating profile data, and deleting the profile.
     */
    private void setupListeners() {
        profileImageView.setOnClickListener(v -> handleProfileImageClick());
        buttonSubmit.setOnClickListener(v -> updateUserData(null)); // No image URL to update initially
        buttonDelete.setOnClickListener(v -> deleteUserData());
    }

    /**
     * Handles profile image click actions (upload, delete, or reset to default).
     */
    private void handleProfileImageClick() {
        if (isGalleryImage) {
            confirmDeleteProfileImage();
        } else {
            showImageOptions();
        }
    }

    /**
     * Displays options for uploading a profile picture or resetting to default.
     */
    private void showImageOptions() {
        new AlertDialog.Builder(getContext())
                .setTitle("Profile Picture")
                .setMessage("Would you like to upload a profile picture from the gallery?")
                .setPositiveButton("Yes", (dialog, which) -> openGallery())
                .setNegativeButton("No", (dialog, which) -> resetToDefaultProfilePicture())
                .show();
    }

    /**
     * Confirms if the user wants to delete the current profile picture.
     */
    private void confirmDeleteProfileImage() {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Profile Picture")
                .setMessage("Do you want to delete the current profile picture?")
                .setPositiveButton("Yes", (dialog, which) -> resetToDefaultProfilePicture())
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Resets the profile picture to the default initials-based image.
     */
    private void resetToDefaultProfilePicture() {
        setDefaultProfilePicture();
        isGalleryImage = false;
        updateUserData(null); // Clear image URL in Firestore
    }

    /**
     * Opens the device gallery to allow the user to select a profile picture.
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Sets a default profile picture based on the user's initials.
     */
    private void setDefaultProfilePicture() {
        if (userName != null && !userName.isEmpty()) {
            profileImageView.setImageDrawable(createInitialsDrawable(getInitials(userName)));
        }
    }

    /**
     * Extracts initials from the user's name for the default profile picture.
     *
     * @param name The user's full name.
     * @return A string containing the initials.
     */
    private String getInitials(String name) {
        String[] parts = name.split(" ");
        return parts.length >= 2 ? parts[0].substring(0, 1) + parts[1].substring(0, 1) : parts[0].substring(0, 1);
    }

    /**
     * Creates a drawable with the user's initials for the default profile picture.
     *
     * @param initials The initials to display.
     * @return A Drawable object containing the initials.
     */
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

    /**
     * Loads the user's data from Firestore and populates the profile fields.
     */
    private void loadUserData() {
        DocumentReference docRef = db.collection("user").document(deviceID);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                editTextName.setText(documentSnapshot.getString("name"));
                editTextPhone.setText(documentSnapshot.getString("phoneNumber"));
                editTextEmail.setText(documentSnapshot.getString("email"));
                editTextLocation.setText(documentSnapshot.getString("location"));
                nameTextView.setText(documentSnapshot.getString("name") != null ? documentSnapshot.getString("name") : "User");
                userName = documentSnapshot.getString("name");
                setDefaultProfilePicture();
            } else {
                Toast.makeText(getContext(), "No user data found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to load user data", Toast.LENGTH_SHORT).show());
    }

    /**
     * Updates the user's data in Firestore with the entered details.
     *
     * @param imageUrl The URL of the profile picture, if updated.
     */
    private void updateUserData(@Nullable String imageUrl) {
        String name = editTextName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || location.isEmpty()) {
            Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("name", name);
        userDetails.put("phoneNumber", phone);
        userDetails.put("email", email);
        userDetails.put("location", location);
        if (imageUrl != null) {
            userDetails.put("profileImageUrl", imageUrl);
        }

        db.collection("user").document(deviceID).update(userDetails)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show());
    }

    /**
     * Deletes the user's profile from Firestore.
     */
    private void deleteUserData() {
        db.collection("user").document(deviceID).delete()
                .addOnSuccessListener(aVoid -> navigateToSignupScreen())
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to delete profile", Toast.LENGTH_SHORT).show());
    }

    /**
     * Navigates to the SignupActivity after deleting the user's profile.
     */
    private void navigateToSignupScreen() {
        Intent intent = new Intent(getContext(), SignupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
