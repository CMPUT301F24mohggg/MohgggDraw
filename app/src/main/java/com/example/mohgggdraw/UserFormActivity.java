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
 * UserFormActivity allows the user to input their details based on user type (entrant, organizer, or admin)
 * and saves this information to Firebase Firestore.
 */
public class UserFormActivity extends AppCompatActivity {

    private EditText editTextName, editTextPhone, editTextEmail, editTextLocation;
    private Button buttonSubmit;
    private String userType; // The type of user: "entrant", "organizer", or "admin"
    private FirebaseFirestore db;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private static final int REQUEST_CHECK_SETTINGS = 2000;

    /**
     * Initializes the activity, setting up Firestore, UI elements, and button listeners.
     *
     * @param savedInstanceState The saved instance state from the previous instance, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_form);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextLocation = findViewById(R.id.editTextLocation);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        // Get user type from intent
        userType = getIntent().getStringExtra("userType");

        // Set hints based on user type
        if ("organizer".equals(userType)) {
            editTextName.setHint("Organizer Name");
        } else if ("admin".equals(userType)) {
            editTextName.setHint("Admin Name");
        } else {
            editTextName.setHint("Entrant Name");
        }

        // Request location permission
        requestLocationPermission();

        // Set up the submit button listener
        buttonSubmit.setOnClickListener(view -> submitUserData());
    }

    /**
     * Requests the user's location permission.
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

        task.addOnSuccessListener(locationSettingsResponse -> {
            // All location settings are satisfied, continue to request location updates
            getUserLocation();
        }).addOnFailureListener(exception -> {
            if (exception instanceof ResolvableApiException) {
                // Location settings are not satisfied, prompt user to enable it
                try {
                    ResolvableApiException resolvable = (ResolvableApiException) exception;
                    resolvable.startResolutionForResult(UserFormActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignore the error
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
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                // User agreed to make required location settings changes
                getUserLocation();
            } else {
                Toast.makeText(this, "Location settings must be enabled to proceed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Gets the user's current location and updates the location EditText field.
     */
    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        String locationString = location.getLatitude() + ", " + location.getLongitude();
                        editTextLocation.setText(locationString);
                    }
                }
            });
        }
    }

    /**
     * Validates user input and saves the user data to Firestore if validation passes.
     */
    private void submitUserData() {
        Log.d("UserFormActivity", "submitUserData() called");

        String name = editTextName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || location.isEmpty()) {
            Toast.makeText(UserFormActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            Log.d("UserFormActivity", "Fields are empty, not proceeding with data submission");
            return;
        }

        String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("UserFormActivity", "Device ID: " + deviceID);

        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("deviceID", deviceID);
        userDetails.put("phoneNumber", phone);
        userDetails.put("email", email);
        userDetails.put("location", location);
        userDetails.put("name", name);
        userDetails.put("userType", getUserTypeCode(userType)); // Store the userType as a numeric code
        addLists(deviceID);




        db.collection("user").document(deviceID)
                .set(userDetails)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(UserFormActivity.this, "User data saved successfully", Toast.LENGTH_SHORT).show();
                    Log.d("UserFormActivity", "Data saved successfully");
                    navigateToHomeScreen();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(UserFormActivity.this, "Failed to save data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("UserFormActivity", "Failed to save data: " + e.getMessage());
                });
    }

    /**
     * Returns a numeric code based on the user type for easier storage.
     *
     * @param userType The type of user: "entrant", "organizer", or "admin".
     * @return An integer code representing the user type.
     */
    int getUserTypeCode(String userType) {
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
     * Navigates to the MainActivity, clearing any existing activities in the task stack.
     */
    private void navigateToHomeScreen() {
        Intent intent = new Intent(UserFormActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("navigateToHomeFragment", true);
        startActivity(intent);
        finish();
    }
    public void addLists(String deviceID){
        DocumentReference mydoc = db.collection("user").document(deviceID);
        Task<DocumentSnapshot> query = mydoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map data =  documentSnapshot.getData();
                if(!data.containsKey("waitList")){
                    DocumentReference mydoc = db.collection("user").document(deviceID);
                    mydoc.update("waitList",new ArrayList<String>());
                    mydoc.update("entrantList",new ArrayList<String>());
                    mydoc.update("createdList",new ArrayList<String>());

                }




            }

        });

    }
}
