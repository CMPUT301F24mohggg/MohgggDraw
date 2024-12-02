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
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView profileImageView, editProfileIcon;
    private TextView nameTextView;
    private EditText editTextName, editTextPhone, editTextEmail, editTextLocation;
    private Button buttonSubmit;
    private Toolbar toolbar;

    private StorageReference storageReference;
    private FirebaseFirestore db;
    private String deviceID;
    private String userName;
    private String originalName, originalPhone, originalEmail;
    private boolean isGalleryImage = false;

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

    private void initViews(View view) {
        profileImageView = view.findViewById(R.id.profileImageView);
        editProfileIcon = view.findViewById(R.id.editProfileIcon);
        nameTextView = view.findViewById(R.id.nameTextView);
        editTextName = view.findViewById(R.id.editTextName);
        editTextPhone = view.findViewById(R.id.editTextPhone);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextLocation = view.findViewById(R.id.editTextLocation);
        buttonSubmit = view.findViewById(R.id.buttonSubmit);
        toolbar = view.findViewById(R.id.toolbar);

        // Hide location field
        editTextLocation.setVisibility(View.GONE);

        // Disable the Update Profile button initially
        buttonSubmit.setEnabled(false);
        buttonSubmit.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.gray));

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

    private void setupListeners() {
        editProfileIcon.setOnClickListener(v -> showProfileOptionsDialog());

        buttonSubmit.setOnClickListener(v -> updateUserData(null)); // Update user data

        // Enable Update Profile button when fields change
        TextWatcher inputWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean isModified = isDataModified();
                buttonSubmit.setEnabled(isModified);
                buttonSubmit.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(),
                        isModified ? R.color.purple_500 : R.color.gray));
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        // Attach TextWatcher to input fields
        editTextName.addTextChangedListener(inputWatcher);
        editTextPhone.addTextChangedListener(inputWatcher);
        editTextEmail.addTextChangedListener(inputWatcher);
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
            resetToDefaultProfilePicture();
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
        if (originalName != null && !originalName.isEmpty()) {
            profileImageView.setImageDrawable(createInitialsDrawable(getInitials(originalName)));

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


    private void loadUserData() {
        DocumentReference docRef = db.collection("user").document(deviceID);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                originalName = documentSnapshot.getString("name");
                originalPhone = documentSnapshot.getString("phoneNumber");
                originalEmail = documentSnapshot.getString("email");

                editTextName.setText(originalName);
                editTextPhone.setText(originalPhone);
                editTextEmail.setText(originalEmail);

                nameTextView.setText(originalName != null ? originalName : "User");

                String profileImageUrl = documentSnapshot.getString("profileImageUrl");
                if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                    Glide.with(this)
                            .load(profileImageUrl)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(createInitialsDrawable(getInitials(originalName)))
                            .into(profileImageView);
                } else {
                    setDefaultProfilePicture();
                }
            } else {
                Toast.makeText(getContext(), "No user data found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Log.e("ProfileFragment", "Failed to load user data: " + e.getMessage(), e));
    }

    private void updateUserData(@Nullable String imageUrl) {
        String name = editTextName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();


        if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create user data map with optional image URL
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("name", name);
        userDetails.put("phoneNumber", phone);
        userDetails.put("email", email);
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

    private boolean isDataModified() {
        String name = editTextName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();

        return !name.equals(originalName) || !phone.equals(originalPhone) || !email.equals(originalEmail);
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

