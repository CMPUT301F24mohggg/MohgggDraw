package com.example.mohgggdraw;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/***
 * Content fragment to display entrants for the first tab.
 */
public class WaitlistEntrantContentFragment extends Fragment implements SetListView {
    private ArrayList<String> dataList;
    private Event event;
    ListView entrantListContainer;
    FloatingActionButton mapButton;
    private ArrayList<String> selectedList = new ArrayList<String>();
    WaitlistViewEntrantsFragment waitlistViewEntrantsFragment;

    public void setEvent(Event event){
        this.event=event;
    }
    public void setFragment(WaitlistViewEntrantsFragment waitlistViewEntrantsFragment){
        this.waitlistViewEntrantsFragment = waitlistViewEntrantsFragment;

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_waiting_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        entrantListContainer = view.findViewById(R.id.listContainer);
        mapButton = view.findViewById(R.id.mapButton);
        mapButton.setVisibility(View.VISIBLE);

        // Populate entrant list dynamically
        if(event!=null) {
            dataList = event.getWaitingList();
            WaitlistEntrantContentAdapter adapter = new WaitlistEntrantContentAdapter(getContext(),dataList,this);
            updateList(adapter);
        }else{
            new WaitinglistDB().setListFromDB("EventWaitinglist", this, event);
        }
        mapButton.setOnClickListener(v->{
            if(waitlistViewEntrantsFragment!=null){
                waitlistViewEntrantsFragment.switchToMap();
            }
           

        });

    }
    //updates selected list for user who have been button clicked for deletion
    public void updateSelectedList(String entrant){
        if (!selectedList.contains(entrant)) {
            selectedList.add(entrant);
            Log.d("dsaf", "i got here!!" + selectedList.toString());
            updateButton();
        } else {
            selectedList.remove(entrant);
            updateButton();
        }

    }




    public void updateButton(){
        if(selectedList.size()>0){
            mapButton.setVisibility(View.VISIBLE);
        }
        else {
            mapButton.setVisibility(View.INVISIBLE);
        }
    }




    @Override
    public Context retContext() {
        return getContext();
    }


    @Override
    public void updateList(ArrayAdapter adapter) {
        entrantListContainer.setAdapter(adapter);


    }
    public void refreshAdapter(){
        new WaitinglistDB().setListFromDBSelected("EventWaitinglist", this, event);


    }
}
