package com.example.mohgggdraw;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_review);

        // Back button logic
        Button backButton = findViewById(R.id.back_button);
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
        TextView titleTextView = findViewById(R.id.review_event_title);
        titleTextView.setText(eventTitle);

        TextView locationTextView = findViewById(R.id.review_event_location);
        locationTextView.setText(eventLocation);

        TextView registrationOpenTextView = findViewById(R.id.review_registration_open);
        registrationOpenTextView.setText(registrationOpen);

        TextView registrationDeadlineTextView = findViewById(R.id.review_registration_deadline);
        registrationDeadlineTextView.setText(registrationDeadline);

        TextView eventStartDateTextView = findViewById(R.id.review_event_start_date);
        eventStartDateTextView.setText(eventStartDate);

        TextView maxPoolingSampleTextView = findViewById(R.id.review_max_pooling_sample);
        maxPoolingSampleTextView.setText(maxPoolingSample);

        TextView maxEntrantsTextView = findViewById(R.id.review_max_entrants);
        maxEntrantsTextView.setText(maxEntrants);

        TextView geolocationTextView = findViewById(R.id.review_geolocation);
        geolocationTextView.setText(enableGeolocation ? "✔" : "X");

        // Set up button
        Button createEventButton = findViewById(R.id.button_create_event);
        createEventButton.setOnClickListener(v -> {
            // Add logic for creating the event, like sending data to a server or saving in a database
        });
    }
}