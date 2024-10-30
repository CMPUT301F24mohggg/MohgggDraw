package com.example.mohgggdraw;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private Button toggleUserButton, toggleFacilityButton, signUpButton;
    private EditText nameField, facilityNameField, emailField, phoneNumberField, locationField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        toggleUserButton = findViewById(R.id.toggleUser);
        toggleFacilityButton = findViewById(R.id.toggleFacility);
        signUpButton = findViewById(R.id.signUpButton);
        nameField = findViewById(R.id.nameField);
        facilityNameField = findViewById(R.id.facilityNameField);
        emailField = findViewById(R.id.emailField);
        phoneNumberField = findViewById(R.id.phoneNumberField);
        locationField = findViewById(R.id.locationField);

        // Set up button listeners
        toggleUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUserFields();
            }
        });

        toggleFacilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFacilityFields();
            }
        });

        // Default to user signup fields
        showUserFields();
    }

    private void showUserFields() {
        nameField.setVisibility(View.VISIBLE);
        facilityNameField.setVisibility(View.GONE);
        toggleUserButton.setBackgroundTintList(getResources().getColorStateList(R.color.purple_700));
        toggleFacilityButton.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
    }

    private void showFacilityFields() {
        nameField.setVisibility(View.GONE);
        facilityNameField.setVisibility(View.VISIBLE);
        toggleFacilityButton.setBackgroundTintList(getResources().getColorStateList(R.color.purple_700));
        toggleUserButton.setBackgroundTintList(getResources().getColorStateList(R.color.purple_200));
    }
}
