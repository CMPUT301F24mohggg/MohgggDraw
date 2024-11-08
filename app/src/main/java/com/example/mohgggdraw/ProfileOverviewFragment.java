package com.example.mohgggdraw;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileOverviewFragment extends Fragment {

    private TextView userNameTextView;
    private TextView userEmailTextView;
    private ImageView profileImageView;
    private FirebaseFirestore db;
    private String deviceID;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        userNameTextView = view.findViewById(R.id.userNameTextView);
        userEmailTextView = view.findViewById(R.id.userEmailTextView);
        profileImageView = view.findViewById(R.id.profileImageView); // Ensure this matches your layout ID
        db = FirebaseFirestore.getInstance();
        deviceID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Load user data
        loadUserData();

        Button buttonEditProfile = view.findViewById(R.id.buttonEditProfile);
        Button buttonLogout = view.findViewById(R.id.buttonLogout);
        Switch switchNotifications = view.findViewById(R.id.switchNotifications);

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

    private void loadUserData() {
        DocumentReference docRef = db.collection("user").document(deviceID);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String userName = documentSnapshot.getString("name");
                String userEmail = documentSnapshot.getString("email");

                userNameTextView.setText(userName != null ? userName : "User Name");
                userEmailTextView.setText(userEmail != null ? userEmail : "user@example.com");

                if (userName != null && !userName.isEmpty()) {
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

    private String getInitials(String name) {
        String[] parts = name.split(" ");
        return parts.length >= 2 ? parts[0].substring(0, 1) + parts[1].substring(0, 1) : parts[0].substring(0, 1);
    }

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
}
