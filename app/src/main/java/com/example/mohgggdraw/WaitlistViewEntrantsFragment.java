package com.example.mohgggdraw;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class WaitlistViewEntrantsFragment extends Fragment {
    private ArrayList<String> sliderData;
    private final Event event;

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

        // Initialize the TabLayout and ViewPager2
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);

        // Sample data for the slider (replace with actual data)
        sliderData = new ArrayList<>();
        sliderData.add("Welcome to the Waitlist");
        sliderData.add("Event Information");
        sliderData.add("Other Highlights");
        sliderData.add("Additional Page");

        // Set up ViewPager2 with adapter
        ViewPagerAdapter adapter = new ViewPagerAdapter(sliderData, requireContext());
        viewPager.setAdapter(adapter);

        // Set up TabLayout with ViewPager2 using TabLayoutMediator
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(sliderData.get(position))).attach();

        // Initialize dataList and populate the entrant list if needed
        ArrayList<String> dataList = event.getWaitingList();  // Ensure this method returns an ArrayList<String>
        LinearLayout entrantListContainer = view.findViewById(R.id.listContainer);

        // Iterate through dataList and add each item programmatically
        for (String entrant : dataList) {
            // Inflate the custom layout for each entrant
            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.entrant_item_layout, entrantListContainer, false);
            TextView userName = itemView.findViewById(R.id.userName);
            userName.setText(entrant);
            entrantListContainer.addView(itemView);
        }
    }
}
