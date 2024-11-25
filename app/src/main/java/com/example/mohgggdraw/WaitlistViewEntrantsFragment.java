package com.example.mohgggdraw;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

/***
 * Fragment to view waitlist
 * ***/
public class WaitlistViewEntrantsFragment extends Fragment {
    private ArrayList<String> dataList;
    private waitlistEntrantAdapter entrantAdapter;
    private ListView entrantList;
    private Event event;

    public WaitlistViewEntrantsFragment(Event event) {
        this.event = event;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.fragment_waiting_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataList = event.getWaitingList();

        LinearLayout entrantListContainer = view.findViewById(R.id.listContainer);

        // Iterate through dataList and add each item programmatically
        for (String entrant : dataList) {
            // Inflate the custom layout for each entrant
            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.entrant_item_layout, entrantListContainer, false);

            // Set entrant's name in the TextView
            TextView userName = itemView.findViewById(R.id.userName);
            userName.setText(entrant);

            // Add the inflated itemView to the LinearLayout container
            entrantListContainer.addView(itemView);
        }
    }
}
