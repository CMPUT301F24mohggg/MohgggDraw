package com.example.mohgggdraw;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button startSignupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Reference the "Start Signup" button
        startSignupButton = findViewById(R.id.start_signup_button);

        // Set click listener to navigate to SignupStep1Activity
        startSignupButton.setOnClickListener(v -> {
            // Start SignupStep1Activity
            Intent intent = new Intent(MainActivity.this, SignupStep1Activity.class);
            startActivity(intent);
        });
    }
}