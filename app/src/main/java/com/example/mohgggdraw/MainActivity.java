package com.example.mohgggdraw;

import android.content.Context;
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

/**
 * MainActivity is the entry point of the application after a user logs in or signs up.
 * This activity manages:
 * <ul>
 * <li>Fragment navigation through BottomNavigationView for entrants, organizers, and admins.</li>
 * <li>User authentication and role-based navigation.</li>
 * <li>Fragment transactions and lifecycle management.</li>
 * <li>Signup redirection if the user is not logged in.</li>
 * </ul>
 */
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
    private Button signupButton;

    /**
     * Initializes the activity, sets up Firebase, BottomNavigationView, and user role-based navigation.
     *
     * @param savedInstanceState A Bundle containing the saved state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        signupLayout = findViewById(R.id.signup_layout);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fragmentContainer = findViewById(R.id.fragment_container);
        signupButton = findViewById(R.id.signup_button);

        // Check for UI component initialization
        if (signupLayout == null || bottomNavigationView == null || fragmentContainer == null || signupButton == null) {
            Toast.makeText(this, "Failed to initialize UI components.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Initially hide navigation and fragments
        fragmentContainer.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.GONE);
        signupLayout.setVisibility(View.GONE);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Get device ID for user authentication
        String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("MainActivity", "Device ID: " + deviceID);

        // Initialize fragment maps
        initializeFragments();

        // Set up signup button listener
        signupButton.setOnClickListener(v -> {
            Intent signupIntent = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(signupIntent);
        });

        // Check if user is logged in and initialize role-based navigation
        checkAndInitializeUser(deviceID);

        if (savedInstanceState != null) {
            activeFragment = getSupportFragmentManager().getFragment(savedInstanceState, "activeFragment");
        }
    }

    /**
     * Initializes fragments for different user roles (entrant, organizer, admin).
     */
    private void initializeFragments() {
        // Fragments for entrants
        entrantFragmentMap.put(R.id.nav_home, new HomeFragment());
        entrantFragmentMap.put(R.id.nav_scanQr, new ScannerFragment());
        entrantFragmentMap.put(R.id.nav_notifications, new NotificationFragment());
        entrantFragmentMap.put(R.id.nav_profile, new ProfileOverviewFragment());

        // Fragments for organizers
        organizerFragmentMap.put(R.id.nav_home, new HomeFragment());
        organizerFragmentMap.put(R.id.nav_create, new CreateFragment());
        organizerFragmentMap.put(R.id.nav_scanQr, new ScannerFragment());
        organizerFragmentMap.put(R.id.nav_notifications, new NotificationFragment());
        organizerFragmentMap.put(R.id.nav_profile, new ProfileOverviewFragment());

        // Fragments for admins
        adminFragmentMap.put(R.id.nav_home, new HomeFragment());
        adminFragmentMap.put(R.id.nav_create, new BrowseProfilesFragment());
        adminFragmentMap.put(R.id.nav_scanQr, new ScannerFragment());
        adminFragmentMap.put(R.id.nav_notifications, new NotificationFragment());
        adminFragmentMap.put(R.id.nav_profile, new ProfileOverviewFragment());
    }

    /**
     * Checks if the user is logged in and sets up navigation based on their role.
     *
     * @param deviceID The unique device ID to identify the user.
     */
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
                            showSignupLayout();
                            return;
                    }
                    initializeNavigation();
                } else {
                    showSignupLayout();
                }
            } else {
                isUserLoggedIn = false;
                showSignupLayout();
            }
        }).addOnFailureListener(e -> Log.e("MainActivity", "Failed to fetch user data: " + e.getMessage()));
    }

    /**
     * Shows the signup layout for new users or when no user is logged in.
     */
    private void showSignupLayout() {
        signupLayout.setVisibility(View.VISIBLE);
        fragmentContainer.setVisibility(View.GONE);
        bottomNavigationView.setVisibility(View.GONE);
    }

    /**
     * Initializes the navigation for the BottomNavigationView.
     */
    private void initializeNavigation() {
        signupLayout.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);
        bottomNavigationView.setVisibility(View.VISIBLE);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (!isClickAllowed()) return false;
            if (activeFragmentMap == null) return false;
            Fragment selectedFragment = activeFragmentMap.get(item.getItemId());
            if (selectedFragment != null) {
                switchFragment(selectedFragment);
                return true;
            }
            return false;
        });

        Fragment defaultFragment = activeFragmentMap.getOrDefault(R.id.nav_home, new HomeFragment());
        if (activeFragment == null || !defaultFragment.equals(activeFragment)) {
            switchFragment(defaultFragment);
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }

        preLoadNotifications();
    }

    /**
     * Preloads the notifications fragment for better user experience.
     */
    private void preLoadNotifications() {
        Fragment notificationFragment = activeFragmentMap.get(R.id.nav_notifications);
        if (notificationFragment == null) {
            notificationFragment = new NotificationFragment();
            activeFragmentMap.put(R.id.nav_notifications, notificationFragment);
        }
    }

    /**
     * Switches to the selected fragment.
     *
     * @param fragment The fragment to switch to.
     */
    private void switchFragment(@NonNull Fragment fragment) {
        if (activeFragment != null && activeFragment.equals(fragment)) return;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setReorderingAllowed(true);

        if (activeFragment != null) transaction.hide(activeFragment);

        String tag = fragment.getClass().getSimpleName();
        Fragment existingFragment = getSupportFragmentManager().findFragmentByTag(tag);

        if (existingFragment != null) {
            transaction.show(existingFragment);
            activeFragment = existingFragment;
        } else {
            transaction.add(R.id.fragment_container, fragment, tag);
            activeFragment = fragment;
        }

        transaction.commitNowAllowingStateLoss();
    }

    /**
     * Prevents rapid clicks to avoid multiple fragment replacements.
     *
     * @return True if the click is allowed, false otherwise.
     */
    private boolean isClickAllowed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > 500) {
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

        if (!isClickAllowed()) return;

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
