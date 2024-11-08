package com.example.mohgggdraw;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private LinearLayout signupLayout;
    private FirebaseFirestore db;
    private final Map<Integer, Fragment> fragmentMap = new HashMap<>();
    private Fragment activeFragment;
    private BottomNavigationView bottomNavigationView;
    private boolean isUserLoggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views and Firestore
        signupLayout = findViewById(R.id.signup_layout);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        db = FirebaseFirestore.getInstance();



        // Hide BottomNavigationView initially
        bottomNavigationView.setVisibility(View.GONE);
        // Initialize fragments
        fragmentMap.put(R.id.nav_home, new HomeFragment());
        fragmentMap.put(R.id.nav_create, new CreateFragment());
        fragmentMap.put(R.id.nav_notifications, new NotificationFragment());
        fragmentMap.put(R.id.nav_myEvents, new MyEventsFragment());
        fragmentMap.put(R.id.nav_profile, new ProfileOverviewFragment());

        // Check intent for navigation flag to home fragment
        boolean navigateToHomeFragment = getIntent().getBooleanExtra("navigateToHomeFragment", false);
        if (navigateToHomeFragment) {
            isUserLoggedIn = true;
            navigateToHomeScreen();
            return;
        }

        // Set up BottomNavigationView item selection listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (isUserLoggedIn) {
                Fragment selectedFragment = fragmentMap.get(item.getItemId());
                if (selectedFragment != null) {
                    switchFragment(selectedFragment);
                }
            } else {
                Toast.makeText(MainActivity.this, "Please sign up or log in to access this feature", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        // Get device ID
        String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("MainActivity", "Device ID: " + deviceID);

        // Check if device ID exists in Firestore
        checkDeviceIDInFirestore(deviceID);
    }

    private void checkDeviceIDInFirestore(String deviceID) {
        db.collection("user").document(deviceID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Device ID exists, proceed to home screen
                isUserLoggedIn = true;
                navigateToHomeScreen();
            } else {
                // Device ID does not exist, show signup option
                signupLayout.setVisibility(View.VISIBLE);
                findViewById(R.id.buttonSignup).setOnClickListener(view -> {
                    Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                    startActivity(intent);
                    finish();
                });
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(MainActivity.this, "Failed to check device ID: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("MainActivity", "Failed to check device ID: " + e.getMessage());
        });
    }

    private void navigateToHomeScreen() {
        // Hide Signup Layout
        signupLayout.setVisibility(View.GONE);

        // Show BottomNavigationView
        bottomNavigationView.setVisibility(View.VISIBLE);

        // Set default fragment to HomeFragment
        Fragment homeFragment = fragmentMap.get(R.id.nav_home);
        if (homeFragment != null) {
            switchFragment(homeFragment);
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    private void switchFragment(@NonNull Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();

        // Set the new active fragment
        activeFragment = fragment;
    }
}
