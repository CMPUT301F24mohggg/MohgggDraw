package com.example.mohgggdraw;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

/***
 * Content fragment to display
 */
public class WaitlistEntrantContentRedrawFragment extends Fragment {
    private ArrayList<String> dataList;
    private Event event;

    public WaitlistEntrantContentRedrawFragment(Event event) {
        this.event = event;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_waiting_list_redraw, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataList = event.getWaitingList();

        LinearLayout entrantListContainer = view.findViewById(R.id.listContainer);

        // Populate entrant list dynamically
        for (String entrant : dataList) {
            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.entrant_item_layout, entrantListContainer, false);
            TextView userName = itemView.findViewById(R.id.userName);
            userName.setText(entrant);
            entrantListContainer.addView(itemView);
        }
    }
}
