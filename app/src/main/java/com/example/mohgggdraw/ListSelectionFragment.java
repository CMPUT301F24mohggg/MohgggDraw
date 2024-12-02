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
/**
 * This fragment provides a user interface for selecting lists associated with an event.
 * Users can select one or more lists and proceed to the next step in the process.
 */
public class ListSelectionFragment extends Fragment {
    private Event event;
    private List<String> selectedLists = new ArrayList<>();
    private ListSelectionListener listener;
    /**
     * Constructs a new ListSelectionFragment.
     *
     * @param event    The event associated with the selection process.
     * @param listener The listener to notify when lists are selected.
     */
    public ListSelectionFragment(Event event, ListSelectionListener listener) {
        this.event = event;
        this.listener = listener;
    }

    /**
     * Inflates the layout for this fragment.
     *
     * @param inflater  LayoutInflater to inflate the views in this fragment.
     * @param container The parent view to attach this fragment to.
     * @param savedInstanceState The previously saved state of the fragment, if any.
     * @return The root view of the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_selection, container, false);
    }

    /**
     * Called after the fragment's view is created.
     * Sets up the checkboxes, click listeners, and Next button behavior.
     *
     * @param view The view returned by {@link #onCreateView}.
     * @param savedInstanceState The previously saved state of the fragment, if any.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View btnNext = view.findViewById(R.id.btn_next);

        // Store references to checkboxes
        CheckBox cbWaitingList = view.findViewById(R.id.cb_waiting_list);
        CheckBox cbSelectedList = view.findViewById(R.id.cb_selected_list);
        CheckBox cbConfirmedList = view.findViewById(R.id.cb_confirmed_list);
        CheckBox cbCancelledList = view.findViewById(R.id.cb_cancelled_list);

        // Set up individual checkboxes with their containers
        setupCheckbox(
                view.findViewById(R.id.ll_waiting_list),
                cbWaitingList,
                "EventWaitinglist"
        );
        setupCheckbox(
                view.findViewById(R.id.ll_selected_list),
                cbSelectedList,
                "EventSelectedlist"
        );
        setupCheckbox(
                view.findViewById(R.id.ll_confirmed_list),
                cbConfirmedList,
                "EventConfirmedlist"
        );
        setupCheckbox(
                view.findViewById(R.id.ll_cancelled_list),
                cbCancelledList,
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

    /**
     * Configures a checkbox and its associated container.
     *
     * @param container   The view container for the checkbox.
     * @param checkBox    The checkbox to configure.
     * @param chosenList  The identifier for the list associated with this checkbox.
     */
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
    /**
     * Retrieves the event associated with the fragment.
     *
     * @return The event instance.
     */
    public Event getEvent() {
        return event; // Provide access to the event
    }

    /**
     * Interface for communicating list selection events to a parent fragment or activity.
     */
    public interface ListSelectionListener {
        /**
         * Called when one or more lists are selected.
         *
         * @param event         The event associated with the selected lists.
         * @param selectedLists The list of selected list identifiers.
         */
        void onListsSelected(Event event, List<String> selectedLists);
    }

}