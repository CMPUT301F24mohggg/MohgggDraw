package com.example.mohgggdraw;

import android.content.Context;
import android.os.Bundle;
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

    public void setEvent(Event event){
        this.event=event;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_waiting_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataList = event.getWaitingList();

        entrantListContainer = view.findViewById(R.id.listContainer);

        // Populate entrant list dynamically
        if(event!=null) {
            WaitlistEntrantContentAdapter adapter = new WaitlistEntrantContentAdapter(getContext(),dataList,this);
            updateList(adapter);
        }
    }



    @Override
    public Context retContext() {
        return getContext();
    }

    @Override
    public void updateButton() {

    }

    @Override
    public void updateList(ArrayAdapter adapter) {
        entrantListContainer.setAdapter(adapter);


    }
}
