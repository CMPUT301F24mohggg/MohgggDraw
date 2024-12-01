package com.example.mohgggdraw;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.example.mohgggdraw.databinding.ActivityMainBinding;
import com.google.firebase.firestore.Query;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/***
 * Fragment to display Array of lists
 * used to display list of user events
 * ***/
public class EventListDisplayFragment extends Fragment {

    private ArrayList<Event> dataList;
    private ListView eventList;
    private EventAdapter eventAdapter;
    private User user = new User();
    private final Map<Integer, Fragment> fragmentMap = new HashMap<>();
    private ViewPager2 viewPager2;
    private HomeFragment fragment;

    public EventListDisplayFragment(User user, HomeFragment page){
        this.user =user;
        this.fragment = page;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        return inflater.inflate(R.layout.event_adapter_layout, container, false);
    }
        //return inflater.inflate(R.layout.fragment_home, container, false);

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ListView
        eventList = view.findViewById(R.id.eventList);
        dataList = new ArrayList<>();

        // Call WaitinglistDB query and handle the callback
        dataList = new WaitinglistDB().queryAllWithWaitingList(this);

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
                Event event = (Event) list.getItemAtPosition(i);
                fragment.goToNextPage(event);
            }
        });
    }

    //updates list when data is changed
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


}