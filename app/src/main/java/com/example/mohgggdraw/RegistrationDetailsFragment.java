package com.example.mohgggdraw;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Calendar;

/***
 This fragment handles the registration details for an event. It:
 - Allows setting of registration open date, deadline, event start time, and event end time
 - Uses DatePickerDialogs and TimePickerDialogs for easy date and time selection
 - Saves the selected dates to the SharedViewModel
 ***/
public class RegistrationDetailsFragment extends Fragment {

    private SharedViewModel sharedViewModel;
    private EditText registrationOpenEditText, registrationDeadlineEditText, eventStartTimeEditText, eventEndTimeEditText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration_details, container, false);

        registrationOpenEditText = view.findViewById(R.id.registration_open);
        registrationDeadlineEditText = view.findViewById(R.id.registration_deadline);
        eventStartTimeEditText = view.findViewById(R.id.event_start_time);
        eventEndTimeEditText = view.findViewById(R.id.event_end_time);

        registrationOpenEditText.setOnClickListener(v -> showDatePickerDialog(registrationOpenEditText, "registrationOpen"));
        registrationDeadlineEditText.setOnClickListener(v -> showDatePickerDialog(registrationDeadlineEditText, "registrationDeadline"));
        eventStartTimeEditText.setOnClickListener(v -> showDateTimePickerDialog(eventStartTimeEditText, "eventStartTime"));
        eventEndTimeEditText.setOnClickListener(v -> showDateTimePickerDialog(eventEndTimeEditText, "eventEndTime"));

        return view;
    }

    private void showDatePickerDialog(EditText editText, String field) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
            String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
            editText.setText(selectedDate);

            // Update ViewModel based on field name
            switch (field) {
                case "registrationOpen":
                    sharedViewModel.setRegistrationOpen(selectedDate);
                    break;
                case "registrationDeadline":
                    sharedViewModel.setRegistrationDeadline(selectedDate);
                    break;
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    private void showDateTimePickerDialog(EditText editText, String field) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
            // After selecting the date, open the time picker dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (timeView, selectedHour, selectedMinute) -> {
                String selectedDateTime = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear + " " + String.format("%02d:%02d", selectedHour, selectedMinute);
                editText.setText(selectedDateTime);

                // Update ViewModel based on field name
                switch (field) {
                    case "eventStartTime":
                        sharedViewModel.setEventStartTime(selectedDateTime);
                        break;
                    case "eventEndTime":
                        sharedViewModel.setEventEndTime(selectedDateTime);
                        break;
                }
            }, hour, minute, true);

            timePickerDialog.show();
        }, year, month, day);

        datePickerDialog.show();
    }

    public void saveData() {
    }

}
