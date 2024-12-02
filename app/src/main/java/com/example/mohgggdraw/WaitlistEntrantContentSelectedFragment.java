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
 * Content fragment to display.
 */
public class WaitlistEntrantContentSelectedFragment extends Fragment implements SetListView{
    private ArrayList<String> dataList;
    private Event event;
    private ListView entrantListContainer;
   boolean viewCreated=false;
    private ArrayList<String> selectedList = new ArrayList<String>();
    FloatingActionButton deleteButton;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_waiting_list_selected, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //dataList = event.getWaitingList();
        entrantListContainer = view.findViewById(R.id.listContainer);
        deleteButton = view.findViewById(R.id.deleteButton);
        deleteButton.setVisibility(View.INVISIBLE);

        viewCreated = true;
        if(event!=null) {
            new WaitinglistDB().setListFromDBSelected("EventSelectedlist", this, event);
        }
        deleteButton.setOnClickListener(v->{
            new WaitinglistDB().removeFromList("EventSelectedlist",selectedList, event);
            refreshAdapter();
            selectedList = new ArrayList<String>();
            updateButton();

        });

    }


    public void setEvent(Event event){
        this.event = event;

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



    @Override
    public Context retContext(){
        return getContext();
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
    public void updateList(ArrayAdapter adapter) {
        entrantListContainer.setAdapter(adapter);

    }
    public void refreshAdapter(){
        new WaitinglistDB().setListFromDBSelected("EventSelectedlist", this, event);

    }
}