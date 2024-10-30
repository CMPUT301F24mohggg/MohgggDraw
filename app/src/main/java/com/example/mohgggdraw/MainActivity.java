package com.example.mohgggdraw;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mohgggdraw.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Event> dataList;
    private ListView eventList;
    private EventAdapter eventAdapter;
    private User user = new User();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        dataList = new ArrayList<Event>();
        for(int i=0;i<10;i++){
            dataList.add(new Event());
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

                Intent intent = new Intent(MainActivity.this,WaitlistFragment.class);
                intent.putExtra("event",event);
                intent.putExtra("user",user);
                startActivity(intent);


            }
        });
    }

}