package com.example.mohgggdraw;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;

public class ParticipationSettingsActivity extends AppCompatActivity {

    private EditText maxPoolingSampleEditText;
    private EditText maxEntrantsEditText;
    private Switch geolocationSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_participation_settings);

        // Initialize fields
        maxPoolingSampleEditText = findViewById(R.id.maximum_pooling_sample);
        maxEntrantsEditText = findViewById(R.id.max_entrants_optional);
        geolocationSwitch = findViewById(R.id.switch_enable_geolocation);

        // Back button logic
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        // Next button logic
        Button nextButton = findViewById(R.id.next_button);
        nextButton.setOnClickListener(v -> {
            // Retrieve data from input fields
            String maxPoolingSample = maxPoolingSampleEditText.getText().toString().trim();
            String maxEntrants = maxEntrantsEditText.getText().toString().trim();
            boolean enableGeolocation = geolocationSwitch.isChecked();

            // Get previous data from Intent extras
            Intent intent = new Intent(ParticipationSettingsActivity.this, ReviewActivity.class);
            intent.putExtra("eventTitle", getIntent().getStringExtra("eventTitle"));
            intent.putExtra("eventLocation", getIntent().getStringExtra("eventLocation"));
            intent.putExtra("registrationOpen", getIntent().getStringExtra("registrationOpen"));
            intent.putExtra("registrationDeadline", getIntent().getStringExtra("registrationDeadline"));
            intent.putExtra("eventStartDate", getIntent().getStringExtra("eventStartDate"));

            // Pass data from Participation Settings
            intent.putExtra("maxPoolingSample", maxPoolingSample);
            intent.putExtra("maxEntrants", maxEntrants);
            intent.putExtra("enableGeolocation", enableGeolocation);

            // Start the ReviewActivity
            startActivity(intent);
        });
    }
}