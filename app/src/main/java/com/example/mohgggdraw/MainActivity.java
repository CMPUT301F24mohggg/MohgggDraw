package com.example.mohgggdraw;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button buttonSignup;
    private LinearLayout signupLayout;
    private FirebaseFirestore db;
    private final Map<Integer, Fragment> fragmentMap = new HashMap<>();
    private Fragment activeFragment;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSignup = findViewById(R.id.buttonSignup);
        signupLayout = findViewById(R.id.signup_layout);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        db = FirebaseFirestore.getInstance();

        // Hide BottomNavigationView initially
        bottomNavigationView.setVisibility(View.GONE);

        // Get device ID
        String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("MainActivity", "Device ID: " + deviceID);

        // Check if intent contains flag to navigate to home
        boolean navigateToHome = getIntent().getBooleanExtra("navigateToHome", false);
        if (navigateToHome) {
            navigateToHomeScreen();
            return;
        }

        // Check if device ID exists in Firestore
        db.collection("user").document(deviceID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Device ID exists, let user enter the app
                Toast.makeText(MainActivity.this, "Welcome back!", Toast.LENGTH_SHORT).show();
                navigateToHomeScreen();
            } else {
                // Device ID does not exist, show signup option
                signupLayout.setVisibility(View.VISIBLE);
                buttonSignup.setOnClickListener(view -> {
                    Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                    startActivity(intent);
                });
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(MainActivity.this, "Failed to check device ID: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("MainActivity", "Failed to check device ID: " + e.getMessage());
        });

        // Initialize fragments
        fragmentMap.put(R.id.nav_home, new HomeFragment());
        fragmentMap.put(R.id.nav_create, new CreateFragment());
        fragmentMap.put(R.id.nav_notifications, new NotificationFragment());
        fragmentMap.put(R.id.nav_myEvents, new MyEventsFragment());
        fragmentMap.put(R.id.nav_profile, new ProfileOverviewFragment());

        // Set up BottomNavigationView item selection listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = fragmentMap.get(item.getItemId());
            if (selectedFragment != null) {
                if (selectedFragment instanceof ProfileOverviewFragment) {
                    // Go to Profile Overview and hide other fragments
                    for (Fragment fragment : fragmentMap.values()) {
                        if (fragment != selectedFragment && fragment.isAdded()) {
                            getSupportFragmentManager().beginTransaction().hide(fragment).commit();
                        }
                    }
                }
                switchFragment(selectedFragment, item.getItemId());
            }
            return true;
        });
    }

    private void navigateToHomeScreen() {
        // Hide Signup Layout
        signupLayout.setVisibility(View.GONE);

        // Show BottomNavigationView
        bottomNavigationView.setVisibility(View.VISIBLE);

        // Set default fragment to HomeFragment
        Fragment homeFragment = fragmentMap.get(R.id.nav_home);
        if (homeFragment != null && activeFragment != homeFragment) {
            switchFragment(homeFragment, R.id.nav_home);
        }
    }

    private void switchFragment(@NonNull Fragment fragment, int fragmentTagId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (!fragment.isAdded()) {
            transaction.add(R.id.fragment_container, fragment, String.valueOf(fragmentTagId));
        }

        // Hide active fragment and show the selected fragment
        if (activeFragment != null) {
            transaction.hide(activeFragment);
        }
        transaction.show(fragment).commit();

        // Set the new active fragment
        activeFragment = fragment;
    }

}
