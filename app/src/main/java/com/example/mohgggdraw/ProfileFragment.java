package com.example.mohgggdraw;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
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
 * This fragment represents the user's profile screen.
 * It allows users to:
 * - View and update their profile details
 * - Upload a profile picture
 * - Delete their account
 * - Navigate to other parts of the app
 */
public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView profileImageView;
    private ImageView editProfileIcon;
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
     * Inflates the layout for the profile screen.
     *
     * @param inflater The LayoutInflater object that can be used to inflate views in the fragment.
     * @param container The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous state.
     * @return The view for the fragment's UI.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize UI elements and Firebase instances
        initViews(view);
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        // Get device ID
        deviceID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Load existing user data
        loadUserData();

        // Set up click listeners
        setupListeners();

        return view;
    }

    /**
     * Initializes all the views and toolbar for the fragment.
     *
     * @param view The root view of the fragment.
     */
    private void initViews(View view) {
        profileImageView = view.findViewById(R.id.profileImageView);
        editProfileIcon = view.findViewById(R.id.editProfileIcon);
        nameTextView = view.findViewById(R.id.nameTextView);
        editTextName = view.findViewById(R.id.editTextName);
        editTextPhone = view.findViewById(R.id.editTextPhone);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextLocation = view.findViewById(R.id.editTextLocation);
        buttonSubmit = view.findViewById(R.id.buttonSubmit);
        buttonDelete = view.findViewById(R.id.buttonDelete);
        toolbar = view.findViewById(R.id.toolbar);

        // Set up the toolbar with back button functionality
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
     * Sets up listeners for buttons and profile image actions.
     */
    private void setupListeners() {
        editProfileIcon.setOnClickListener(v -> showProfileOptionsDialog());

        buttonSubmit.setOnClickListener(v -> updateUserData(null)); // Initially no image URL to update
        buttonDelete.setOnClickListener(v -> deleteUserData());
    }

    /**
     * Handles the profile image click. Allows the user to either upload a new picture or delete the current one.
     */
    private void showProfileOptionsDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_box_profile);

        // views
        Button btnUpload = dialog.findViewById(R.id.btnUpload);
        Button btnDelete = dialog.findViewById(R.id.btnDelete);
        TextView btnCancel = dialog.findViewById(R.id.btnCancel);

        // Handling click listeners
        btnUpload.setOnClickListener(v -> {
            openGallery();
            dialog.dismiss();
        });

        btnDelete.setOnClickListener(v -> { // in case of delete
            setDefaultProfilePicture();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }



    /**
     * Resets the profile picture to the default initials-based placeholder.
     */
    private void resetToDefaultProfilePicture() {
        setDefaultProfilePicture();
        isGalleryImage = false;
        updateUserData(null); // Clear image URL in Firestore
    }

    /**
     * Opens the device gallery to allow the user to pick an image.
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Sets a default profile picture using the user's initials.
     */
    private void setDefaultProfilePicture() {
        if (userName != null && !userName.isEmpty()) {
            profileImageView.setImageDrawable(createInitialsDrawable(getInitials(userName)));

        }
    }

    /**
     * Extracts initials from a user's name.
     *
     * @param name The user's name.
     * @return A string containing the initials.
     */
    private String getInitials(String name) {
        String[] parts = name.split(" ");
        return parts.length >= 2 ? parts[0].substring(0, 1) + parts[1].substring(0, 1) : parts[0].substring(0, 1);
    }

    /**
     * Creates a drawable containing the user's initials.
     *
     * @param initials The initials to display.
     * @return A Drawable containing the initials.
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
     * Handles the result of the gallery activity for picking an image.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            handleGalleryResult(data.getData());
        }
    }

    /**
     * Creates a circular version of the bitmap
     * @param bitmap
     * @return circular cropped bitmap object
     */
    private Bitmap circularBitmap(Bitmap bitmap) {
        int size = Math.min(bitmap.getWidth(), bitmap.getHeight());
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);

        // Draws the circular shape
        float radius = size / 2f;
        canvas.drawCircle(radius, radius, radius, paint);

        // Crops the image (to fit the circular frame)
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, -((bitmap.getWidth() - size) / 2f), -((bitmap.getHeight() - size) / 2f), paint);

        return output;
    }

    /**
     * Processes the result from the gallery and uploads the image to Firebase.
     *
     * @param imageUri The URI of the selected image.
     */
    private void handleGalleryResult(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);

            Bitmap circularBitmap = circularBitmap(bitmap);
            profileImageView.setImageBitmap(circularBitmap);

            isGalleryImage = true;
            uploadProfilePictureToFirebase(circularBitmap);
        } catch (IOException e) {
            Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
            Log.e("ProfileFragment", "Error loading image", e);
        }
    }



    /**
     * Uploads the selected profile picture to Firebase Storage.
     *
     * @param bitmap The bitmap of the selected image.
     */
    private void uploadProfilePictureToFirebase(Bitmap bitmap) {
        byte[] data = getImageData(bitmap);
        StorageReference profileRef = storageReference.child("profile_pictures/" + deviceID + ".jpg");

        profileRef.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> profileRef.getDownloadUrl().addOnSuccessListener(uri -> updateUserData(uri.toString())))
                .addOnFailureListener(e -> Log.e("ProfileFragment", "Error uploading profile picture", e));
    }

    /**
     * Converts a bitmap into a byte array for uploading.
     *
     * @param bitmap The bitmap to convert.
     * @return A byte array representation of the bitmap.
     */
    private byte[] getImageData(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * Loads the user's data from Firestore and populates the UI fields.
     */
    private void loadUserData() {
        DocumentReference docRef = db.collection("user").document(deviceID);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Log.d("ProfileFragment", "Document exists. Populating fields...");
                editTextName.setText(documentSnapshot.getString("name"));
                editTextPhone.setText(documentSnapshot.getString("phoneNumber"));
                editTextEmail.setText(documentSnapshot.getString("email"));
                editTextLocation.setText(documentSnapshot.getString("location"));
                nameTextView.setText(documentSnapshot.getString("name") != null ? documentSnapshot.getString("name") : "User");
                userName = documentSnapshot.getString("name");
                setDefaultProfilePicture(); // Set profile picture using user's name initials
            } else {
                Log.d("ProfileFragment", "No user data found for this device ID.");
                Toast.makeText(getContext(), "No user data found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.e("ProfileFragment", "Failed to load user data: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Failed to load user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Updates the user's data in Firestore, including an optional profile image URL.
     *
     * @param imageUrl The URL of the uploaded profile image, if any.
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

        // Create user data map with optional image URL
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("name", name);
        userDetails.put("phoneNumber", phone);
        userDetails.put("email", email);
        userDetails.put("location", location);
        addLists(deviceID);
        if (imageUrl != null) {
            userDetails.put("profileImageUrl", imageUrl);
        }

        // Update user data in Firestore
        db.collection("user").document(deviceID).update(userDetails)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("ProfileFragment", "Failed to update profile: " + e.getMessage());
                });
    }

    /**
     * Deletes the user's data from Firestore.
     */
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

    /**
     * Navigates the user to the signup screen after account deletion.
     */
    private void navigateToSignupScreen() {
        Intent intent = new Intent(getContext(), SignupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    /**
     * Adds default lists (waitList, entrantList, createdList) to the user's Firestore document if they don't exist.
     *
     * @param deviceID The unique ID of the user's device.
     */
    public void addLists(String deviceID) {
        DocumentReference mydoc = db.collection("user").document(deviceID);
        Task<DocumentSnapshot> query = mydoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map data = documentSnapshot.getData();
                if (!data.containsKey("waitList")) {
                    DocumentReference mydoc = db.collection("user").document(deviceID);
                    mydoc.update("waitList", new ArrayList<String>());
                    mydoc.update("entrantList", new ArrayList<String>());
                    mydoc.update("createdList", new ArrayList<String>());
                }
            }
        });
    }
}
