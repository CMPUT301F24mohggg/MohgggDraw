package com.example.mohgggdraw;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/***
 * Fragment to display Array of lists
 * used to display list of user events
 * ***/
public class EventListDisplayFragment extends Fragment implements EventListView{

    private ArrayList<Event> dataList;
    private ListView eventList;
    private EventAdapter eventAdapter;
    private HomeFragment fragment;
    private String deviceID;


    public void setFragment(HomeFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void setEventList(ArrayList<Event> events) {
        dataList = events;
        dataChange();


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

        //pulling all data for test purpose
        //dataList = new WaitinglistDB().queryAllWithWaitingList(this);


        eventList = view.findViewById(R.id.eventList);
        new UserDB().queryList("waitList",this,deviceID);
        eventAdapter = new EventAdapter(this.getContext(), dataList);
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

    @Override
    public void setList(ArrayList list) {

    }

    public void setDevice(String id){
        this.deviceID = id;

    }

}