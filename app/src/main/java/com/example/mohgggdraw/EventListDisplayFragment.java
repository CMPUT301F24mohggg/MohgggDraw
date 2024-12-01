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

    // Setter method for fragment (from second implementation)
    public void setFragment(HomeFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.event_adapter_layout, container, false);
    }

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

    // Updates list when data is changed
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

    // Implement interface method to set event list
    @Override
    public void setEventList(ArrayList<Event> events) {
        dataList = events;
        dataChange();
    }

    // Additional interface method implementation
    @Override
    public void setList(ArrayList list) {
        // Optional: Implement if needed
    }

    // Setter for device ID
    public void setDevice(String id) {
        this.deviceID = id;
    }
}