package com.example.mohgggdraw;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminEventListView extends EventListDisplayFragment {
    private ArrayList<Event> dataList;
    private ListView eventList;
    private EventAdapter eventAdapter;
    private User user = new User();
    private final Map<Integer, Fragment> fragmentMap = new HashMap<>();
    private ViewPager2 viewPager2;
    private HomeFragment fragment;

    // Constructor
    public AdminEventListView(User user, HomeFragment page) {
        super(user, page);
        this.fragment = page; // Properly assign the fragment
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        return inflater.inflate(R.layout.event_adapter_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        dataList = new ArrayList<>();
        TextView title = view.findViewById(R.id.event_view_title);
        title.setText("Admin Event View");

        // Pulling all data for test purposes
        dataList = new WaitinglistDB().queryAllWithWaitingList(this);


        eventAdapter = new EventAdapter(this.getContext(), dataList);
        eventList = view.findViewById(R.id.eventList);

        eventList.setAdapter(eventAdapter);

        // OnClickListener for each event item
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> list, View view, final int i, long id) {
                Event event = (Event) list.getItemAtPosition(i);
                if (fragment != null) {
                    fragment.goToNextPage(event);
                } else {
                    // Log or handle gracefully
                    Log.e("AdminEventView", "Fragment is null. Cannot navigate to the next page.");
                }
            }
        });
    }

    // Updates the list when data changes
    public void dataChange() {
        eventAdapter = new EventAdapter(this.getContext(), dataList);
        eventList.setAdapter(eventAdapter);
    }
}
