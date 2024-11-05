package com.example.mohgggdraw;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;

public class ReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_review);

        // Back button logic
        ImageView backButton = findViewById(R.id.back_button); // Updated to ImageView if it’s an icon
        backButton.setOnClickListener(v -> finish()); // Goes back to the previous activity

        // Get data from the intent
        Intent intent = getIntent();
        String eventTitle = intent.getStringExtra("eventTitle");
        String eventLocation = intent.getStringExtra("eventLocation");
        String registrationOpen = intent.getStringExtra("registrationOpen");
        String registrationDeadline = intent.getStringExtra("registrationDeadline");
        String eventStartDate = intent.getStringExtra("eventStartDate");
        String maxPoolingSample = intent.getStringExtra("maxPoolingSample");
        String maxEntrants = intent.getStringExtra("maxEntrants");
        boolean enableGeolocation = intent.getBooleanExtra("enableGeolocation", false);

        // Set the values in the view
        TextView titleTextView = findViewById(R.id.text_event_title);
        titleTextView.setText(eventTitle);

        TextView locationTextView = findViewById(R.id.text_event_location);
        locationTextView.setText(eventLocation);

        TextView registrationOpenTextView = findViewById(R.id.text_event_open_date);
        registrationOpenTextView.setText(registrationOpen);

        TextView registrationDeadlineTextView = findViewById(R.id.text_event_close_date);
        registrationDeadlineTextView.setText(registrationDeadline);

        TextView eventStartDateTextView = findViewById(R.id.text_event_start_date);
        eventStartDateTextView.setText(eventStartDate);

        TextView maxPoolingSampleTextView = findViewById(R.id.text_max_pooling_sample); // Updated to match XML
        maxPoolingSampleTextView.setText(maxPoolingSample);

        TextView maxEntrantsTextView = findViewById(R.id.text_max_entrants); // Updated to match XML
        maxEntrantsTextView.setText(maxEntrants);

        // Update the checkbox based on geolocation setting
        CheckBox geolocationCheckbox = findViewById(R.id.checkbox_enable_geolocation);
        geolocationCheckbox.setChecked(enableGeolocation);

        // Set up create event button
        Button createEventButton = findViewById(R.id.button_create_event);
        createEventButton.setOnClickListener(v -> {
            // Add logic for creating the event, like sending data to a server or saving in a database
        });
    }
}
