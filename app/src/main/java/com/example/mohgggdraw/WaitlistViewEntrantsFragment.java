package com.example.mohgggdraw;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

/***
 * Fragment to view waitlist
 *
 * ***/

public class WaitlistViewEntrantsFragment extends Fragment {
    private ArrayList<String > dataList;
    private waitlistEntrantAdapter entrantAdapter;
    private ListView entrantList;
    private Event event;

    public WaitlistViewEntrantsFragment(Event event) {
        this.event = event;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        return inflater.inflate((R.layout.waitlist_view_entrants), container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //creates data from events waitlist to display
        dataList = event.getWaitingList();

        entrantAdapter = new waitlistEntrantAdapter(this.getContext(), dataList);
        entrantList = view.findViewById(R.id.entrantslist);

        entrantList.setAdapter(entrantAdapter);

    }

}
