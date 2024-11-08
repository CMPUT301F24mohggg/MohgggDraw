package com.example.mohgggdraw;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

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
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){

    dataList = new ArrayList<Event>();
//        for(int i=0;i<10;i++){
//            Event event = new Event("olKgM5GAgkLRUqo97eVS","testname","testname","https://firebasestorage.googleapis.com/v0/b/mohgggdraw.appspot.com/o/event_images%2F1730963184849.jpg?alt=media&token=8c93f3c0-2e18-494a-95ec-a95b864ccdbd","testname","testname","testname","testname","testname",true);
//            dataList.add(event);
//            new WaitinglistDB().updateWaitlist(event);
//
//        }
        //pulling all data for test purpose
        dataList = new WaitinglistDB().queryAllWithWaitingList(this);

        eventAdapter = new EventAdapter(this.getContext(), dataList);
        eventList = view.findViewById(R.id.eventList);

        eventList.setAdapter(eventAdapter);

//onclick per event item
        eventList.setOnItemClickListener(new  android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> list, View view,
                                    final int i, long id) {

                Event event = (Event) list.getItemAtPosition(i);
                fragment.goToNextPage(event);


            }
        });
    }
    //updates list when data is changed
    public void dataChange(){

        eventAdapter = new EventAdapter(this.getContext(), dataList);

        eventList.setAdapter(eventAdapter);

    }

}