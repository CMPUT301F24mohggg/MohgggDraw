package com.example.mohgggdraw;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class ListSelectionFragment extends Fragment {
    private Event event;
    private List<String> selectedLists = new ArrayList<>();
    private ListSelectionListener listener;

    public ListSelectionFragment(Event event, ListSelectionListener listener) {
        this.event = event;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View btnNext = view.findViewById(R.id.btn_next);

        // Set up individual checkboxes with their containers
        setupCheckbox(
                view.findViewById(R.id.ll_waiting_list),
                view.findViewById(R.id.cb_waiting_list),
                "EventWaitinglist"
        );
        setupCheckbox(
                view.findViewById(R.id.ll_selected_list),
                view.findViewById(R.id.cb_selected_list),
                "EventSelectedlist"
        );
        setupCheckbox(
                view.findViewById(R.id.ll_confirmed_list),
                view.findViewById(R.id.cb_confirmed_list),
                "EventConfirmedlist"
        );
        setupCheckbox(
                view.findViewById(R.id.ll_cancelled_list),
                view.findViewById(R.id.cb_cancelled_list),
                "EventCancelledlist"
        );

        // Set up the Next button
        btnNext.setOnClickListener(v -> {
            if (!selectedLists.isEmpty()) {
                // Notify the listener with the selected lists
                listener.onListsSelected(event, selectedLists);
            } else {
                Toast.makeText(requireContext(), "Please select at least one recipient", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setupCheckbox(View container, CheckBox checkBox, String chosenList) {
        if (container == null || checkBox == null) return;

        // Set the initial state if needed
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedLists.add(chosenList);
            } else {
                selectedLists.remove(chosenList);
            }
            Log.d("ListSelectionFragment", "Selected Lists: " + selectedLists);
        });

        // Toggle the checkbox when the LinearLayout is clicked
        container.setOnClickListener(v -> {
            boolean newCheckedState = !checkBox.isChecked();
            checkBox.setChecked(newCheckedState); // This will trigger the OnCheckedChangeListener
        });
    }


    public List<String> getSelectedLists() {
        return new ArrayList<>(selectedLists); // Return a copy to avoid accidental modification
    }

    public Event getEvent() {
        return event; // Provide access to the event
    }

    // Interface for communication with parent fragments
    public interface ListSelectionListener {
        void onListsSelected(Event event, List<String> selectedLists);
    }
}
