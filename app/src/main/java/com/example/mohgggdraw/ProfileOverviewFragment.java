package com.example.mohgggdraw;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * ProfileOverviewFragment displays an overview of the user's profile, including:
 * <ul>
 * <li>User's name and email</li>
 * <li>Profile picture</li>
 * <li>Options to edit the profile or log out</li>
 * </ul>
 */
public class ProfileOverviewFragment extends Fragment {

    private TextView userNameTextView;
    private TextView userEmailTextView;
    private ImageView profileImageView;
    private FirebaseFirestore db;
    private String deviceID;
    private Switch notificationSwitch;

    /**
     * Inflates the layout for the fragment.
     *
     * @param inflater           The LayoutInflater used to inflate the layout.
     * @param container          The parent view that this fragment's UI is attached to.
     * @param savedInstanceState The saved state of the fragment, if available.
     * @return The created View for the fragment.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_overview, container, false);
    }

    /**
     * Initializes the fragment's views and sets up click listeners for buttons.
     *
     * @param view               The root view of the fragment.
     * @param savedInstanceState The saved state of the fragment, if available.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        userNameTextView = view.findViewById(R.id.userNameTextView);
        userEmailTextView = view.findViewById(R.id.userEmailTextView);
        profileImageView = view.findViewById(R.id.profileImageView);
        db = FirebaseFirestore.getInstance();
        notificationSwitch = view.findViewById(R.id.notificationSwitch);
        deviceID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> updateNotification(isChecked));
        notificationPreference();
        // Load user data
        loadUserData();

        // Initialize buttons and switches
        Button buttonEditProfile = view.findViewById(R.id.buttonEditProfile);
        Button buttonLogout = view.findViewById(R.id.buttonLogout);


        // Set onClickListener for Edit Profile button to navigate to ProfileFragment
        buttonEditProfile.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ProfileFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Set onClickListener for Logout button
        buttonLogout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    /**
     * Loads user data from Firebase Firestore and updates the UI elements.
     */
    private void loadUserData() {
        DocumentReference docRef = db.collection("user").document(deviceID);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String userName = documentSnapshot.getString("name");
                String userEmail = documentSnapshot.getString("email");
                String profileImageUrl = documentSnapshot.getString("profileImageUrl");

                userNameTextView.setText(userName != null ? userName : "User Name");
                userEmailTextView.setText(userEmail != null ? userEmail : "user@example.com");

                if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                    Glide.with(this)
                            .asBitmap()
                            .load(profileImageUrl)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(createInitialsDrawable(getInitials(userName)))
                            .error(createInitialsDrawable(getInitials(userName)))
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    profileImageView.setImageBitmap(circularBitmap(resource));
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                    profileImageView.setImageDrawable(placeholder);
                                }
                            });
                } else {
                    // Show initials if no profile image is set
                    profileImageView.setImageDrawable(createInitialsDrawable(getInitials(userName)));
                }
            } else {
                Toast.makeText(getContext(), "No user data found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.e("ProfileOverviewFragment", "Failed to load user data: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
        });
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

        float radius = size / 2f;
        canvas.drawCircle(radius, radius, radius, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, -((bitmap.getWidth() - size) / 2f), -((bitmap.getHeight() - size) / 2f), paint);

        return output;
    }


    /**
     * Extracts the initials from the user's name for use in a default profile image.
     *
     * @param name The full name of the user.
     * @return A string containing the initials.
     */
    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "U"; // Default to "U" for User if name is empty
        String[] parts = name.trim().split(" ");
        return parts.length >= 2 ? parts[0].substring(0, 1) + parts[1].substring(0, 1) : parts[0].substring(0, 1);
    }

    /**
     * Creates a drawable containing the user's initials for the default profile image.
     *
     * @param initials The initials to display in the drawable.
     * @return A Drawable object containing the initials.
     */
    private Drawable createInitialsDrawable(String initials) {
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setTextSize(50);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true); // Smooth text rendering
        canvas.drawText(initials, 50, 65, paint);
        return new BitmapDrawable(getResources(), bitmap);
    }

    /**
     * Updates the user's notification preferences in the Firestore database.
     * @param isEnabled boolean checking if the notifications are enabled
     */
    private void updateNotification(boolean isEnabled) {
        if (!isEnabled) {
            db.collection("notification").document("notificationOptOut")
                    .update("deviceIds", FieldValue.arrayUnion(deviceID))
                    .addOnSuccessListener(aVoid -> Log.d("ProfileFragment", "Opted out of notifications"))
                    .addOnFailureListener(e -> Log.e("ProfileFragment", "Error opting out", e));
        } else {
            db.collection("notification").document("notificationOptOut")
                    .update("deviceIds", FieldValue.arrayRemove(deviceID))
                    .addOnSuccessListener(aVoid -> Log.d("ProfileFragment", "Opted in to notifications"))
                    .addOnFailureListener(e -> Log.e("ProfileFragment", "Error opting in", e));
        }
    }

    /**
     * Loads the user's notification preference from the Firestore database and updates the notification switch UI accordingly.
     * retrieves the list of users who opted out of receiving notifications from document "notificationOptOut"
     */
    private void notificationPreference() {
        db.collection("notification").document("notificationOptOut")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> deviceIDs = (List<String>) documentSnapshot.get("deviceId");
                        notificationSwitch.setChecked(!(deviceIDs != null && deviceIDs.contains(deviceID)));
                    }
                })
                .addOnFailureListener(e -> Log.e("ProfileFragment", "Error loading preference", e));
    }
}
