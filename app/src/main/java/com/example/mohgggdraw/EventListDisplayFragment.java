package com.example.mohgggdraw;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

/***
 * Fragment to display Array of lists
 * used to display list of user events
 * ***/
public class EventListDisplayFragment extends Fragment implements EventListView {

    private ArrayList<Event> dataList;
    private ListView eventList;
    private EventAdapter eventAdapter;
    private HomeFragment fragment;
    private String deviceID;
    private User user;

    // Constructor for the first implementation
    public EventListDisplayFragment(User user, HomeFragment page) {
        this.user = user;
        this.fragment = page;
    }

    // No-arg constructor for the second implementation
    public EventListDisplayFragment() {
        // Default constructor
    }

    /**
     * Sets the HomeFragment instance to navigate back.
     *
     * @param fragment The HomeFragment instance.
     */
    public void setFragment(HomeFragment fragment) {
        this.fragment = fragment;
    }

    /**
     * Inflates the layout for the fragment.
     *
     * @param inflater  LayoutInflater to inflate views in the fragment.
     * @param container The parent view that the fragment's UI is attached to.
     * @param savedInstanceState Bundle with the saved state of the fragment.
     * @return The root view of the fragment.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.event_adapter_layout, container, false);
    }

    /**
     * Called after the view is created. Initializes the list, adapter, and event listeners.
     *
     * @param view The view returned by {@link #onCreateView}.
     * @param savedInstanceState Bundle with the saved state of the fragment.
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize dataList
        dataList = new ArrayList<>();

        // Initialize ListView
        eventList = view.findViewById(R.id.eventList);

        // Determine data fetching method
        if (deviceID != null) {
            // If deviceID is set, use UserDB to query list
            new UserDB().queryList("waitList", this, deviceID);
        } else {
            // Otherwise, use WaitinglistDB to query all events
            dataList = new WaitinglistDB().queryAllWithWaitingList(this);
        }

        // Initialize adapter
        if (getContext() != null) {
            eventAdapter = new EventAdapter(requireContext(), dataList);
            eventList.setAdapter(eventAdapter);
        } else {
            Log.e("EventListDisplayFragment", "Context is null during onViewCreated");
        }

        // Set up item click listener
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> list, View view, final int i, long id) {
                if (fragment != null) {
                    Event event = (Event) list.getItemAtPosition(i);
                    fragment.goToNextPage(event);
                }
            }
        });
    }


    /**
     * Updates the list data and refreshes the adapter.
     */
    public void dataChange() {
        if (getContext() == null || eventList == null) {
            Log.e("EventListDisplayFragment", "Context or eventList is null, skipping dataChange()");
            return;
        }

        if (eventAdapter == null) {
            eventAdapter = new EventAdapter(requireContext(), dataList);
            eventList.setAdapter(eventAdapter);
        } else {
            eventAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Sets the event list data and triggers a UI update.
     *
     * @param events The list of events to display.
     */
    @Override
    public void setEventList(ArrayList<Event> events) {
        dataList = events;
        eventAdapter = new EventAdapter(requireContext(), dataList);
        eventList.setAdapter(eventAdapter);
        dataChange();
    }

    /**
     * Sets a generic list (optional implementation).
     *
     * @param list The list of data to set.
     */
    @Override
    public void setList(ArrayList list) {
        // Optional: Implement if needed
    }


    /**
     * Sets the device ID for querying data.
     *
     * @param id The device ID string.
     */
    public void setDevice(String id) {
        this.deviceID = id;
    }
}