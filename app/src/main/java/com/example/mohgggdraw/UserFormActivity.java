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
    private String userType; // Should be either "entrant", "organizer", or "admin"
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
        userType = getIntent().getStringExtra("userType"); // "entrant", "organizer", or "admin"

        // Set hints based on user type
        if ("organizer".equals(userType)) {
            editTextName.setHint("Organizer Name");
        } else if ("admin".equals(userType)) {
            editTextName.setHint("Admin Name");
        } else {
            editTextName.setHint("Entrant Name");
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
        userDetails.put("name", name);
        userDetails.put("userType", getUserTypeCode(userType)); // Store the user type as 0, 1, or 2

        // Save data to Firestore under the "user" collection with deviceID as the document ID
        db.collection("user").document(deviceID)
                .set(userDetails)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(UserFormActivity.this, "User data saved successfully", Toast.LENGTH_SHORT).show();
                    Log.d("UserFormActivity", "Data saved successfully");
                    navigateToHomeScreen(); // Navigate to MainActivity after saving
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(UserFormActivity.this, "Failed to save data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("UserFormActivity", "Failed to save data: " + e.getMessage());
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
        intent.putExtra("navigateToHomeFragment", true); // Pass flag to navigate to home fragment
        startActivity(intent);
        finish();
    }
}
