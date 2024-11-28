package com.example.mohgggdraw;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private final Map<Integer, Fragment> entrantFragmentMap = new HashMap<>();
    private final Map<Integer, Fragment> organizerFragmentMap = new HashMap<>();
    private final Map<Integer, Fragment> adminFragmentMap = new HashMap<>();
    private Map<Integer, Fragment> activeFragmentMap;
    private Fragment activeFragment;
    private BottomNavigationView bottomNavigationView;
    private View fragmentContainer;
    private boolean isUserLoggedIn = false;
    private long lastClickTime = 0;
    private Button signupButton; // Button for navigating to SignupActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        signupLayout = findViewById(R.id.signup_layout);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fragmentContainer = findViewById(R.id.fragment_container);
        signupButton = findViewById(R.id.signup_button); // Signup button initialization

        // Disable state restoration for BottomNavigationView
        bottomNavigationView.setSaveEnabled(false);

        // Check for null components
        if (signupLayout == null || bottomNavigationView == null || fragmentContainer == null || signupButton == null) {
            Toast.makeText(this, "Failed to initialize UI components.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Initially hide navigation and fragments
        fragmentContainer.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.GONE);
        signupLayout.setVisibility(View.GONE);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Get device ID
        String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("MainActivity", "Device ID: " + deviceID);

        // Initialize fragments
        initializeFragments();

        // Set up the click listener for the signup button
        signupButton.setOnClickListener(v -> {
            Intent signupIntent = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(signupIntent);
        });

        // Check and initialize user based on role
        checkAndInitializeUser(deviceID);

        if (savedInstanceState != null) {
            activeFragment = getSupportFragmentManager().getFragment(savedInstanceState, "activeFragment");
        }
    }

    private void initializeFragments() {
        entrantFragmentMap.put(R.id.nav_home, new HomeFragment());
        entrantFragmentMap.put(R.id.nav_scanQr, new ScannerFragment());
        entrantFragmentMap.put(R.id.nav_notifications, new NotificationFragment());
        entrantFragmentMap.put(R.id.nav_profile, new ProfileOverviewFragment());

        organizerFragmentMap.put(R.id.nav_home, new HomeFragment());
        organizerFragmentMap.put(R.id.nav_create, new CreateFragment());
        organizerFragmentMap.put(R.id.nav_scanQr, new ScannerFragment());
        organizerFragmentMap.put(R.id.nav_notifications, new NotificationFragment());
        organizerFragmentMap.put(R.id.nav_profile, new ProfileOverviewFragment());

        adminFragmentMap.put(R.id.nav_home, new HomeFragment());
        adminFragmentMap.put(R.id.nav_create, new AdminFragment());
        adminFragmentMap.put(R.id.nav_scanQr, new ScannerFragment());
        adminFragmentMap.put(R.id.nav_notifications, new NotificationFragment());
        adminFragmentMap.put(R.id.nav_profile, new ProfileOverviewFragment());
    }

    private void checkAndInitializeUser(String deviceID) {
        db.collection("user").document(deviceID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                isUserLoggedIn = true;

                Long userType = documentSnapshot.getLong("userType");
                if (userType != null) {
                    switch (userType.intValue()) {
                        case 0: // Entrant
                            activeFragmentMap = entrantFragmentMap;
                            bottomNavigationView.getMenu().clear();
                            bottomNavigationView.inflateMenu(R.menu.bottom_navigation_menu_entrant);
                            break;
                        case 1: // Organizer
                            activeFragmentMap = organizerFragmentMap;
                            bottomNavigationView.getMenu().clear();
                            bottomNavigationView.inflateMenu(R.menu.bottom_navigation_menu_organizer);
                            break;
                        case 2: // Admin
                            activeFragmentMap = adminFragmentMap;
                            bottomNavigationView.getMenu().clear();
                            bottomNavigationView.inflateMenu(R.menu.bottom_navigation_menu_admin);
                            break;
                        default:
                            Toast.makeText(this, "Unknown user role. Showing signup.", Toast.LENGTH_SHORT).show();
                            showSignupLayout();
                            return;
                    }
                    initializeNavigation();
                } else {
                    Toast.makeText(this, "User role not defined. Showing signup.", Toast.LENGTH_SHORT).show();
                    showSignupLayout();
                }
            } else {
                isUserLoggedIn = false;
                showSignupLayout();
            }
        }).addOnFailureListener(e -> {
            Log.e("MainActivity", "Failed to fetch user data: " + e.getMessage());
            Toast.makeText(MainActivity.this, "Error fetching user data.", Toast.LENGTH_SHORT).show();
        });
    }

    private void showSignupLayout() {
        signupLayout.setVisibility(View.VISIBLE);
        fragmentContainer.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.GONE);
    }

    private void initializeNavigation() {
        signupLayout.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);
        bottomNavigationView.setVisibility(View.VISIBLE);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (!isClickAllowed()) {
                Log.d("MainActivity", "Click blocked to avoid rapid interaction");
                return false; // Block rapid clicks
            }

            if (activeFragmentMap == null) return false;
            Fragment selectedFragment = activeFragmentMap.get(item.getItemId());
            if (selectedFragment != null) {
                switchFragment(selectedFragment);
                return true;
            }
            return false;
        });

        // Default to HomeFragment, but avoid duplicate replacement
        Fragment defaultFragment = activeFragmentMap.getOrDefault(R.id.nav_home, new HomeFragment());
        if (activeFragment == null || !defaultFragment.equals(activeFragment)) {
            switchFragment(defaultFragment);
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    private void switchFragment(@NonNull Fragment fragment) {
        if (activeFragment != null && activeFragment.equals(fragment)) {
            Log.d("MainActivity", "Fragment is already active: " + fragment.getClass().getSimpleName());
            return; // Avoid replacing the fragment if it's already active
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setReorderingAllowed(true);

        if (activeFragment != null) {
            // Hide the currently active fragment
            transaction.hide(activeFragment);
        }

        String tag = fragment.getClass().getSimpleName();
        Fragment existingFragment = getSupportFragmentManager().findFragmentByTag(tag);

        if (existingFragment != null) {
            // If fragment already exists, simply show it
            transaction.show(existingFragment);
            activeFragment = existingFragment;
        } else {
            // If fragment does not exist, add it and tag it
            transaction.add(R.id.fragment_container, fragment, tag);
            activeFragment = fragment;
        }

        // Commit the transaction
        transaction.commitNowAllowingStateLoss();
    }

    private boolean isClickAllowed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > 500) { // Allow clicks only after 500ms
            lastClickTime = currentTime;
            return true;
        }
        return false;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (activeFragment != null) {
            getSupportFragmentManager().putFragment(outState, "activeFragment", activeFragment);
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("activeFragment")) {
            activeFragment = getSupportFragmentManager().getFragment(savedInstanceState, "activeFragment");
            switchFragment(activeFragment);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        if (!isClickAllowed()) return; // Prevent rapid interactions

        boolean navigateToHomeFragment = intent.getBooleanExtra("navigateToHomeFragment", false);
        if (navigateToHomeFragment && activeFragmentMap != null) {
            Fragment homeFragment = activeFragmentMap.getOrDefault(R.id.nav_home, new HomeFragment());
            if (homeFragment != null && !homeFragment.equals(activeFragment)) {
                switchFragment(homeFragment);
                bottomNavigationView.setSelectedItemId(R.id.nav_home);
            }
        }
    }
}