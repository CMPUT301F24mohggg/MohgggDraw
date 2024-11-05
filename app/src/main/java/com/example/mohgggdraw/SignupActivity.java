package com.example.mohgggdraw;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private Button buttonEntrant, buttonFacility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        buttonEntrant = findViewById(R.id.buttonEntrant);
        buttonFacility = findViewById(R.id.buttonFacility);

        buttonEntrant.setOnClickListener(view -> {
            navigateToFormScreen("entrant");
        });

        buttonFacility.setOnClickListener(view -> {
            navigateToFormScreen("facility");
        });
    }

    private void navigateToFormScreen(String userType) {
        Intent intent = new Intent(SignupActivity.this, UserFormActivity.class);
        intent.putExtra("userType", userType);
        startActivity(intent);
    }
}
