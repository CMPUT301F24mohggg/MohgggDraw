package com.example.mohgggdraw;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.HashMap;
import java.util.Map;





public class MainActivity extends AppCompatActivity {

    private Button buttonSignup;
    private FirebaseFirestore db;
    private final Map<Integer, Fragment> fragmentMap = new HashMap<>();
    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSignup = findViewById(R.id.buttonSignup);
        db = FirebaseFirestore.getInstance();

        // Get device ID
        String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("MainActivity", "Device ID: " + deviceID);

        // Check if device ID exists in Firestore
        db.collection("user").document(deviceID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Device ID exists, let user enter the app
                Toast.makeText(MainActivity.this, "Welcome back!", Toast.LENGTH_SHORT).show();
                navigateToHomeScreen();
            } else {
                // Device ID does not exist, show signup option
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
        fragmentMap.put(R.id.nav_profile, new ProfileFragment());

        // Initialize BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set default fragment
        if (savedInstanceState == null) {
            activeFragment = fragmentMap.get(R.id.nav_home);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, activeFragment, "HOME")
                    .commit();
        }

        // Set up BottomNavigationView item selection listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = fragmentMap.get(item.getItemId());
            if (selectedFragment != null) {
                switchFragment(selectedFragment, item.getItemId());
            }
            return true;
        });

        // Display notification badge (if needed)
        bottomNavigationView.getOrCreateBadge(R.id.nav_notifications).setVisible(true);
    }
    private void navigateToHomeScreen() {
        // Navigate to the main/home screen of the app
        Intent intent = new Intent(MainActivity.this, HomeFragment.class);
        startActivity(intent);
        finish();}

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
