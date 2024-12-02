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
public class WaitlistEntrantContentCancelledFragment extends Fragment implements SetListView {
    private ArrayList<String> dataList;
    private Event event;
    ListView entrantListContainer;
    FloatingActionButton redraw;
    WaitlistViewEntrantsFragment frag;


    public void setEvent(Event event){
        this.event = event;
    }
    public void setFragment(WaitlistViewEntrantsFragment frag){
        this.frag =frag;

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_waiting_list_cancelled, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        entrantListContainer = view.findViewById(R.id.listContainer);
        redraw=view.findViewById(R.id.drawButton);
        redraw.setOnClickListener(v->{
                    new RandomWaitlistSelector(event).fillSelected(frag);
                    frag.updateFragments();

                }

                );

        if(event!=null) {
            new WaitinglistDB().setListFromDB("EventCancelledlist", this, event);
        }



        // Populate entrant list dynamically

    }



    @Override
    public Context retContext() {
        return getContext();
    }

    @Override
    public void updateButton() {
        if(true){
            redraw.setVisibility(View.VISIBLE);
        }
        else {
            redraw.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void updateList(ArrayAdapter adapter) {
        entrantListContainer.setAdapter(adapter);

    }

    @Override
    public void updateSelectedList(String entrant) {

    }
}
