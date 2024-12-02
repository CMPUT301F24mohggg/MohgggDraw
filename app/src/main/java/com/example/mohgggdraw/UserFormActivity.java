package com.example.mohgggdraw;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * UserFormActivity handles the user registration process based on user type (entrant, organizer, or admin).
 * It provides fields for user details, handles location permission, fetches the current location,
 * and saves the user data to Firebase Firestore.
 */
public class UserFormActivity extends AppCompatActivity {

    private EditText editTextName, editTextFacilityName, editTextPhone, editTextEmail, editTextLocation;
    private Button buttonSubmit;
    private String userType; // The type of user: "entrant", "organizer", or "admin"
    private FirebaseFirestore db;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private static final int REQUEST_CHECK_SETTINGS = 2000;

    /**
     * Called when the activity is first created.
     * Initializes Firestore, location services, and UI elements.
     *
     * @param savedInstanceState The saved state of the activity, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_form);

        // Initialize Firestore and location services
        db = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Link UI components
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextLocation = findViewById(R.id.editTextLocation);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        // Retrieve user type from intent
        userType = getIntent().getStringExtra("userType");

        // Configure form fields based on user type
        configureFormFields();

        // Request location permission
        requestLocationPermission();

        // Set up the submit button listener
        buttonSubmit.setOnClickListener(view -> submitUserData());
    }

    /**
     * Configures the form fields based on the user type.
     * Shows the Facility Name field only for organizers.
     */
    private void configureFormFields() {
        if ("organizer".equals(userType)) {
            editTextName.setHint("Name");
        } else {
            editTextName.setHint(userType.equals("admin") ? "Name" : "Name");
        }
    }

    /**
     * Requests location permission from the user.
     */
    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            checkLocationSettings();
        }
    }

    /**
     * Handles the result of the location permission request.
     *
     * @param requestCode  The request code passed in requestPermissions().
     * @param permissions  The requested permissions.
     * @param grantResults The grant results for the corresponding permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationSettings();
            } else {
                Toast.makeText(this, "Location permission is required to proceed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Checks if the required location settings are enabled.
     */
    private void checkLocationSettings() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)
                .setFastestInterval(5000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(locationSettingsResponse -> getUserLocation())
                .addOnFailureListener(exception -> {
                    if (exception instanceof ResolvableApiException) {
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) exception;
                            resolvable.startResolutionForResult(UserFormActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException ignored) {
                        }
                    }
                });
    }

    /**
     * Handles the result of the location settings resolution.
     *
     * @param requestCode The request code passed in startResolutionForResult().
     * @param resultCode  The result code returned by the child activity.
     * @param data        An Intent that carries the result data.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS && resultCode == RESULT_OK) {
            getUserLocation();
        } else {
            Toast.makeText(this, "Location settings must be enabled to proceed", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Fetches the user's current location and sets it in the location field.
     */
    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    String locationString = location.getLatitude() + ", " + location.getLongitude();
                    editTextLocation.setText(locationString);
                }
            });
        }
    }

    /**
     * Validates the user input and saves the user data to Firebase Firestore.
     */
    private void submitUserData() {
        String name = editTextName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("deviceID", deviceID);
        userDetails.put("name", name);
        userDetails.put("phoneNumber", phone);
        userDetails.put("email", email);
        userDetails.put("location", location);
        userDetails.put("userType", getUserTypeCode(userType));


        db.collection("user").document(deviceID)
                .set(userDetails)
                .addOnSuccessListener(aVoid -> {
                    addLists(deviceID);
                    Toast.makeText(this, "User data saved successfully", Toast.LENGTH_SHORT).show();
                    navigateToHomeScreen();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /**
     * Adds default lists (waitList, entrantList, createdList) to the user's Firestore document.
     *
     * @param deviceID The unique device ID of the user.
     */
    private void addLists(String deviceID) {
        DocumentReference mydoc = db.collection("user").document(deviceID);
        mydoc.get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> data = documentSnapshot.getData();
            if (data != null && !data.containsKey("waitList")) {
                mydoc.update("waitList", new ArrayList<String>());
                mydoc.update("entrantList", new ArrayList<String>());
                mydoc.update("createdList", new ArrayList<String>());
            }
        });
    }

    /**
     * Converts the user type string into a numeric code.
     *
     * @param userType The type of user (entrant, organizer, or admin).
     * @return A numeric code representing the user type.
     */
    private int getUserTypeCode(String userType) {
        switch (userType) {
            case "organizer":
                return 1;
            case "admin":
                return 2;
            case "entrant":
            default:
                return 0;
        }
    }

    /**
     * Navigates the user to the home screen and clears the activity stack.
     */
    private void navigateToHomeScreen() {
        Intent intent = new Intent(UserFormActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
