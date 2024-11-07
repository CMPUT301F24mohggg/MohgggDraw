package com.example.mohgggdraw;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.os.StrictMode;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mohgggdraw.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import java.util.HashMap;
import java.util.Map;


import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
//import com.google.zxing.MultiFormatWriter;

public class MainActivity extends AppCompatActivity {



    private AppBarConfiguration appBarConfiguration;

    private StorageReference storageReference;
    private FirebaseFirestore db;

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

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                PackageManager.PERMISSION_GRANTED);
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
