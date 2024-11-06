package com.example.mohgggdraw;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final Map<Integer, Fragment> fragmentMap = new HashMap<>();
    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
