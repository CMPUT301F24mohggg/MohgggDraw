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

/***
 This activity serves as the main entry point of the application. It:
 - Initializes Firebase
 - Sets up the bottom navigation view
 - Manages fragment transactions for different navigation items
 - Handles the display of notification badges
 ***/
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

        // Initialize fragments
        fragmentMap.put(R.id.nav_home, new HomeFragment());
        fragmentMap.put(R.id.nav_create, new CreateFragment());
        fragmentMap.put(R.id.nav_notifications, new NotificationFragment());
        fragmentMap.put(R.id.nav_scanQr, new ScannerFragment());
        fragmentMap.put(R.id.nav_profile, new ProfileOverviewFragment());

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

        // Initialize the view based on login status
        checkAndInitializeUser(deviceID);
    }

    private void checkAndInitializeUser(String deviceID) {
        db.collection("user").document(deviceID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // User is logged in, proceed to initialize navigation
                isUserLoggedIn = true;
                initializeNavigation();
            } else {
                // User is not logged in, show signup layout
                isUserLoggedIn = false;
                signupLayout.setVisibility(View.VISIBLE);
                bottomNavigationView.setVisibility(View.GONE); // Hide navigation if not logged in

                findViewById(R.id.buttonSignup).setOnClickListener(view -> {
                    Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                    startActivity(intent);
                });
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(MainActivity.this, "Failed to check device ID: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("MainActivity", "Failed to check device ID: " + e.getMessage());
        });
    }

    private void initializeNavigation() {
        signupLayout.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.VISIBLE);

        // Check if we need to navigate to the home fragment directly
        boolean navigateToHomeFragment = getIntent().getBooleanExtra("navigateToHomeFragment", false);
        if (navigateToHomeFragment || activeFragment == null) {
            // Set default fragment to HomeFragment
            switchFragment(fragmentMap.get(R.id.nav_home));
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    private void switchFragment(@NonNull Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        // Set the new active fragment
        activeFragment = fragment;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // Update the intent in case "navigateToHomeFragment" is set
        initializeNavigation(); // Re-initialize the navigation on new intent
    }
}
