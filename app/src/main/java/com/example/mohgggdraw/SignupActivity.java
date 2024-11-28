package com.example.mohgggdraw;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

/**
 * SignupActivity class handles the selection of user type (Entrant, Facility, or Admin) and navigates
 * to the appropriate form screen based on the user's choice.
 */
public class SignupActivity extends AppCompatActivity {

    private Button buttonEntrant, buttonFacility, buttonAdmin;

    /**
     * Called when the activity is first created.
     * Initializes the layout and sets up button click listeners.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                           being shut down, this Bundle contains the data it most
     *                           recently supplied. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        buttonEntrant = findViewById(R.id.buttonEntrant);
        buttonFacility = findViewById(R.id.buttonFacility);
        buttonAdmin = findViewById(R.id.buttonAdmin);

        buttonEntrant.setOnClickListener(view -> {
            navigateToFormScreen("entrant");
        });

        buttonFacility.setOnClickListener(view -> {
            navigateToFormScreen("facility");
        });

        buttonAdmin.setOnClickListener(view -> {
            navigateToFormScreen("admin");
        });
    }

    /**
     * Navigates to the UserFormActivity, passing in the user type as an extra parameter.
     *
     * @param userType The type of user selected (either "entrant", "facility", or "admin").
     */
    void navigateToFormScreen(String userType) {
        Intent intent = new Intent(SignupActivity.this, UserFormActivity.class);
        intent.putExtra("userType", userType);
        startActivity(intent);
    }
}