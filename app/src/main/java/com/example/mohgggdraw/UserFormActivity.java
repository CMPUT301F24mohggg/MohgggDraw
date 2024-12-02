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

public class UserFormActivity extends AppCompatActivity {

    private EditText editTextName, editTextFacilityName, editTextPhone, editTextEmail, editTextLocation;
    private Button buttonSubmit;
    private String userType;
    private FirebaseFirestore db;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private static final int REQUEST_CHECK_SETTINGS = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_form);

        db = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        editTextName = findViewById(R.id.editTextName);
        editTextFacilityName = findViewById(R.id.editTextFacilityName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextLocation = findViewById(R.id.editTextLocation);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        userType = getIntent().getStringExtra("userType");

        configureFormFields();
        requestLocationPermission();

        buttonSubmit.setOnClickListener(view -> submitUserData());
    }

    private void configureFormFields() {
        if ("organizer".equals(userType)) {
            editTextFacilityName.setVisibility(EditText.VISIBLE);
            editTextName.setHint("Name");
        } else {
            editTextFacilityName.setVisibility(EditText.GONE);
            editTextName.setHint(userType.equals("admin") ? "Admin Name" : "Entrant Name");
        }
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            checkLocationSettings();
        }
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS && resultCode == RESULT_OK) {
            getUserLocation();
        } else {
            Toast.makeText(this, "Location settings must be enabled to proceed", Toast.LENGTH_SHORT).show();
        }
    }

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

    private void submitUserData() {
        String name = editTextName.getText().toString().trim();
        String facilityName = editTextFacilityName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if ("organizer".equals(userType) && facilityName.isEmpty()) {
            Toast.makeText(this, "Facility Name is required for organizers", Toast.LENGTH_SHORT).show();
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
        if ("organizer".equals(userType)) {
            userDetails.put("facilityName", facilityName);
        }

        db.collection("user").document(deviceID)
                .set(userDetails)
                .addOnSuccessListener(aVoid -> {
                    addLists(deviceID);
                    Toast.makeText(this, "User data saved successfully", Toast.LENGTH_SHORT).show();
                    navigateToHomeScreen();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

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

    private void navigateToHomeScreen() {
        Intent intent = new Intent(UserFormActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
