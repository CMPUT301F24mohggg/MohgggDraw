package com.example.mohgggdraw;



import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserFormActivity extends AppCompatActivity {

    private EditText editTextName, editTextPhone, editTextEmail, editTextLocation;
    private Button buttonSubmit;
    private String userType;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_form);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextLocation = findViewById(R.id.editTextLocation);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        // Get user type from intent
        userType = getIntent().getStringExtra("userType");

        // Set hints based on user type
        if ("facility".equals(userType)) {
            editTextName.setHint("Facility Name");
        } else {
            editTextName.setHint("Name");
        }

        // Set up the submit button listener
        buttonSubmit.setOnClickListener(view -> submitUserData());
    }

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

        // Create user data map
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("deviceID", deviceID);
        userDetails.put("phoneNumber", phone);
        userDetails.put("email", email);
        userDetails.put("location", location);

        Log.d("UserFormActivity", "Attempting to save data to Firestore...");

        if ("entrant".equals(userType)) {
            userDetails.put("name", name);
            Log.d("UserFormActivity", "Saving entrant data to Firestore");
            db.collection("user")
                    .document(deviceID)
                    .set(userDetails)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(UserFormActivity.this, "User data saved successfully", Toast.LENGTH_SHORT).show();
                        Log.d("UserFormActivity", "Data saved successfully for entrant");
                        navigateToHomeScreen(); // Navigate to home screen after signup
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(UserFormActivity.this, "Failed to save data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("UserFormActivity", "Failed to save entrant data: " + e.getMessage());
                    });
        } else if ("facility".equals(userType)) {
            userDetails.put("facilityName", name);
            Log.d("UserFormActivity", "Saving facility data to Firestore");
            db.collection("user")
                    .document(deviceID)
                    .set(userDetails)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(UserFormActivity.this, "User data saved successfully", Toast.LENGTH_SHORT).show();
                        Log.d("UserFormActivity", "Data saved successfully for facility");
                        navigateToHomeScreen(); // Navigate to home screen after signup
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(UserFormActivity.this, "Failed to save data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("UserFormActivity", "Failed to save facility data: " + e.getMessage());
                    });
        }
    }

    private void navigateToHomeScreen() {
        Intent intent = new Intent(UserFormActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("navigateToHome", true); // Pass flag to navigate to home fragment
        startActivity(intent);
        finish();
    }
}