package com.example.mohgggdraw;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Calendar;

public class RegistrationDetailsFragment extends Fragment {

    private SharedViewModel sharedViewModel;
    private EditText registrationOpenEditText, registrationDeadlineEditText, eventStartTimeEditText, eventEndTimeEditText;
    private ImageView calendarOpenIcon, calendarDeadlineIcon, calendarStartTimeIcon, calendarEndTimeIcon;

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

        calendarOpenIcon = view.findViewById(R.id.calendar_icon_registration_open);
        calendarDeadlineIcon = view.findViewById(R.id.calendar_icon_registration_deadline);
        calendarStartTimeIcon = view.findViewById(R.id.calendar_icon_event_start_time);
        calendarEndTimeIcon = view.findViewById(R.id.calendar_icon_event_end_time);

        registrationOpenEditText.setOnClickListener(v -> showDatePickerDialog(registrationOpenEditText, "registrationOpen"));
        registrationDeadlineEditText.setOnClickListener(v -> showDatePickerDialog(registrationDeadlineEditText, "registrationDeadline"));
        eventStartTimeEditText.setOnClickListener(v -> showDateTimePickerDialog(eventStartTimeEditText, "eventStartTime"));
        eventEndTimeEditText.setOnClickListener(v -> showDateTimePickerDialog(eventEndTimeEditText, "eventEndTime"));

        // Set ImageView click listeners
        calendarOpenIcon.setOnClickListener(v -> showDatePickerDialog(registrationOpenEditText, "registrationOpen"));
        calendarDeadlineIcon.setOnClickListener(v -> showDatePickerDialog(registrationDeadlineEditText, "registrationDeadline"));
        calendarStartTimeIcon.setOnClickListener(v -> showDateTimePickerDialog(eventStartTimeEditText, "eventStartTime"));
        calendarEndTimeIcon.setOnClickListener(v -> showDateTimePickerDialog(eventEndTimeEditText, "eventEndTime"));

        TextWatcher formWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateNextButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        registrationOpenEditText.addTextChangedListener(formWatcher);
        registrationDeadlineEditText.addTextChangedListener(formWatcher);
        eventStartTimeEditText.addTextChangedListener(formWatcher);
        eventEndTimeEditText.addTextChangedListener(formWatcher);

        return view;
    }

    public boolean isFormValid() {
        return !registrationOpenEditText.getText().toString().isEmpty()
                && !registrationDeadlineEditText.getText().toString().isEmpty()
                && !eventStartTimeEditText.getText().toString().isEmpty()
                && !eventEndTimeEditText.getText().toString().isEmpty();
    }

    private void updateNextButtonState() {
        boolean isValid = isFormValid();
        CreateFragment parentFragment = (CreateFragment) getParentFragment();
        if (parentFragment != null) {
            parentFragment.setNextButtonEnabled(isValid);
        }
    }

    private void showDatePickerDialog(EditText editText, String field) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
            String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
            editText.setText(selectedDate);

            if ("registrationOpen".equals(field)) {
                sharedViewModel.setRegistrationOpen(selectedDate);
            } else if ("registrationDeadline".equals(field)) {
                sharedViewModel.setRegistrationDeadline(selectedDate);
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
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (timeView, selectedHour, selectedMinute) -> {
                String selectedDateTime = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear + " " + selectedHour + ":" + selectedMinute;
                editText.setText(selectedDateTime);

                if ("eventStartTime".equals(field)) {
                    sharedViewModel.setEventStartTime(selectedDateTime);
                } else if ("eventEndTime".equals(field)) {
                    sharedViewModel.setEventEndTime(selectedDateTime);
                }
            }, hour, minute, true);

            timePickerDialog.show();
        }, year, month, day);

        datePickerDialog.show();
    }

    public void saveData() {
        sharedViewModel.setRegistrationOpen(registrationOpenEditText.getText().toString());
        sharedViewModel.setRegistrationDeadline(registrationDeadlineEditText.getText().toString());
        sharedViewModel.setEventStartTime(eventStartTimeEditText.getText().toString());
        sharedViewModel.setEventEndTime(eventEndTimeEditText.getText().toString());
    }
}
