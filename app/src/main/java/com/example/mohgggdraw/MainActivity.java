package com.example.mohgggdraw;



import static android.app.PendingIntent.getActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;

import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.os.StrictMode;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;



import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;


/***
 This activity serves as the main entry point of the application. It:
 - Initializes Firebase
 - Sets up the bottom navigation view
 - Manages fragment transactions for different navigation items
 - Handles the display of notification badges
 ***/

public class MainActivity extends AppCompatActivity {




    private AppBarConfiguration appBarConfiguration;

    private StorageReference storageReference;
    private FirebaseFirestore db;


    private LinearLayout signupLayout;

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
        fragmentMap.put(R.id.nav_myEvents, new ScanQrFragment());
        fragmentMap.put(R.id.nav_profile, new ProfileOverviewFragment());

        // Set up BottomNavigationView item selection listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (isUserLoggedIn) {
                Fragment selectedFragment = fragmentMap.get(item.getItemId());
                if ((selectedFragment != null) && selectedFragment.getClass().getSimpleName().equals("ScanQrFragment")) {
                scanCode();
                return true;
                } 
                else {
                  switchFragment(selectedFragment);
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


    // Scan QR Code
    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan QR Code");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // Update the intent in case "navigateToHomeFragment" is set
        initializeNavigation(); // Re-initialize the navigation on new intent
    }
}

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan QR Code");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    // Result for QR scan
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {

        if ((result.getContents() != null)) {
            String eventId = result.getContents();
            DocToEvent docToEvent = new DocToEvent(eventId);
            docToEvent.getDocSnap();

            if (!docToEvent.isSuccess()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Invalid Event QR Code");

                builder.setMessage("Please scan a valid event QR code.");
                builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
            }
            else {


                Event myevent = docToEvent.createEvent();
                com.example.mohgggdraw.User user = new User();
                Fragment fragment = new WaitlistFragment(myevent, user, new HomeFragment());
                FragmentManager fragmentManager = this.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.addToBackStack(null);

                fragmentTransaction.show(fragment).commit();
            }
        }
    });

}

