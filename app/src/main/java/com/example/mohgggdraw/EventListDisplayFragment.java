package com.example.mohgggdraw;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import com.example.mohgggdraw.databinding.ActivityMainBinding;

import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class EventListDisplayFragment extends AppCompatActivity {

    private ArrayList<Event> dataList;
    private ListView eventList;
    private EventAdapter eventAdapter;
    private User user = new User();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.event_adapter_layout);


        dataList = new ArrayList<Event>();
        for(int i=0;i<10;i++){
            Event event = new Event();
            dataList.add(event);
            new WaitinglistDB(event).updateWaitlist();

        }

        eventAdapter = new EventAdapter(this, dataList);
        eventList = findViewById(R.id.eventList);

        eventList.setAdapter(eventAdapter);
        //setting up new book button

        eventList.setOnItemClickListener(new  android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> list, View view,
                                    final int i, long id) {

                Event event = (Event) list.getItemAtPosition(i);


                Intent intent = new Intent(EventListDisplayFragment.this,WaitlistFragment.class);
                intent.putExtra("event",event);
                intent.putExtra("user",user);
                startActivity(intent);


            }
        });
    }

}