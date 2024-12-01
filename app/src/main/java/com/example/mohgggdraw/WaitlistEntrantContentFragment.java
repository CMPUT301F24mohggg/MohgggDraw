package com.example.mohgggdraw;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/***
 * Content fragment to display entrants for the first tab.
 */
public class WaitlistEntrantContentFragment extends Fragment implements SetListView {
    private ArrayList<String> dataList;
    private Event event;
    ListView entrantListContainer;
    FloatingActionButton deleteButton;
    private ArrayList<String> selectedList = new ArrayList<String>();

    public void setEvent(Event event){
        this.event=event;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_waiting_list_selected, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataList = event.getWaitingList();

        entrantListContainer = view.findViewById(R.id.listContainer);
        deleteButton = view.findViewById(R.id.deleteButton);
        deleteButton.setVisibility(View.INVISIBLE);

        // Populate entrant list dynamically
        if(event!=null) {
            WaitlistEntrantContentSelectedAdapter adapter = new WaitlistEntrantContentSelectedAdapter(getContext(),dataList,this);
            updateList(adapter);
        }
        deleteButton.setOnClickListener(v->{
            new WaitinglistDB().removeFromList("EventWaitinglist",selectedList, event);
           refreshAdapter();

            selectedList = new ArrayList<String>();
            new WaitinglistDB().updateWaitlistInEvent(event);
            updateButton();

        });

    }
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
            deleteButton.setVisibility(View.VISIBLE);
        }
        else {
            deleteButton.setVisibility(View.INVISIBLE);
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
