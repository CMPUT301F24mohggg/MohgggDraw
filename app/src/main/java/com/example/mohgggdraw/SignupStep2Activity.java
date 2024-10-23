package com.example.mohgggdraw;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignupStep2Activity extends AppCompatActivity {

    private EditText dobField, locationField;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_step2);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Reference UI components
        dobField = findViewById(R.id.dob);
        locationField = findViewById(R.id.location);
        Button signupButton = findViewById(R.id.signup_button);

        // Get data passed from the first activity
        String name = getIntent().getStringExtra("name");
        String email = getIntent().getStringExtra("email");
        String password = getIntent().getStringExtra("password");

        // Handle signup button click
        signupButton.setOnClickListener(view -> {
            String dob = dobField.getText().toString().trim();
            String location = locationField.getText().toString().trim();

            // Validate inputs
            if (TextUtils.isEmpty(dob) || TextUtils.isEmpty(location)) {
                Toast.makeText(SignupStep2Activity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create user in Firebase Auth
            assert email != null;
            assert password != null;
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            // Update user profile with the name
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();
                            if (user != null) {
                                user.updateProfile(profileUpdates);

                                // Save additional info in Firestore in Entrant/User-Info structure
                                Map<String, Object> userInfo = new HashMap<>();
                                userInfo.put("email", email);
                                userInfo.put("name", name);
                                userInfo.put("dob", dob);
                                userInfo.put("location", location);

                                // Reference the collection path: Entrant/User-Info
                                db.collection("Entrant")
                                        .document(user.getUid()) // Use the UID as the document ID
                                        .collection("User-Info")  // Store data under the User-Info sub-collection
                                        .add(userInfo)  // Store the data in the sub-collection
                                        .addOnSuccessListener(aVoid ->
                                                Toast.makeText(SignupStep2Activity.this, "Signup successful!", Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(e ->
                                                Toast.makeText(SignupStep2Activity.this, "Failed to save user info: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            }
                        } else {
                            Toast.makeText(SignupStep2Activity.this, "Signup failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}