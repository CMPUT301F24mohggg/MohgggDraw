package com.example.mohgggdraw;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button userSignupButton = findViewById(R.id.userSignupButton);
        Button facilitySignupButton = findViewById(R.id.facilitySignupButton);

        userSignupButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            intent.putExtra("role", 1); // 1 for User
            startActivity(intent);
        });

        facilitySignupButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            intent.putExtra("role", 2); // 2 for Facility
            startActivity(intent);
        });
    }
}