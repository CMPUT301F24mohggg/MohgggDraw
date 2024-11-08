package com.example.mohgggdraw;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
/***
 This activity handles the registration details for an event. It:
 - Retrieves event data (title, location, poster URL) from the previous activity
 - Allows users to set registration open date, deadline, and event start time
 - Uses DatePickerDialog for easy date selection
 - Passes all collected data to the ParticipationSettingsActivity when "Next" is clicked
 - Includes a "Back" button to return to the previous screen
 ***/
public class RegistrationDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_registration_details);

        // Retrieve data passed from Organizer Activity
        String eventTitle = getIntent().getStringExtra("eventTitle");
        String eventLocation = getIntent().getStringExtra("eventLocation");
        String eventPosterUrl = getIntent().getStringExtra("eventPosterUrl");

        // Initialize EditTexts for dates
        EditText registrationOpenEditText = findViewById(R.id.registration_open);
        EditText registrationDeadlineEditText = findViewById(R.id.registration_deadline);
        EditText eventStartTimeEditText = findViewById(R.id.event_start_time);

        // Show date picker when clicking on each EditText
        registrationOpenEditText.setOnClickListener(v -> showDatePickerDialog(registrationOpenEditText));
        registrationDeadlineEditText.setOnClickListener(v -> showDatePickerDialog(registrationDeadlineEditText));
        eventStartTimeEditText.setOnClickListener(v -> showDatePickerDialog(eventStartTimeEditText));

        // Back button logic
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        // Next button logic to pass data to ParticipationSettingsActivity
        Button nextButton = findViewById(R.id.next_button);
        nextButton.setOnClickListener(v -> {
            String registrationOpen = registrationOpenEditText.getText().toString();
            String registrationDeadline = registrationDeadlineEditText.getText().toString();
            String eventStartDate = eventStartTimeEditText.getText().toString();

            Intent intent = new Intent(RegistrationDetailsActivity.this, ParticipationSettingsActivity.class);
            intent.putExtra("eventTitle", eventTitle);
            intent.putExtra("eventLocation", eventLocation);
            intent.putExtra("eventPosterUrl", eventPosterUrl);
            intent.putExtra("registrationOpen", registrationOpen);
            intent.putExtra("registrationDeadline", registrationDeadline);
            intent.putExtra("eventStartDate", eventStartDate);
            startActivity(intent);
        });
    }

    // Method to show date picker and set the selected date in the EditText
    private void showDatePickerDialog(final EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    editText.setText(date);
                }, year, month, day);

        datePickerDialog.show();
    }
}