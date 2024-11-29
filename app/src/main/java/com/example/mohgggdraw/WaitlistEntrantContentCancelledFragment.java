package com.example.mohgggdraw;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/***
 * Content fragment to display entrants for the first tab.
 */
public class WaitlistEntrantContentCancelledFragment extends Fragment implements SetListView {
    private ArrayList<String> dataList;
    private Event event;
    LinearLayout entrantListContainer;

    public void setEvent(Event event){
        this.event = event;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_waiting_list_cancelled, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        entrantListContainer = view.findViewById(R.id.listContainer);

        if(event!=null) {
            new WaitinglistDB().setListFromDB("EventCancelledlist", this, event);
        }



        // Populate entrant list dynamically

    }
    public void startList(Event event){
        this.event = event;

    }


    @Override
    public void setList(ArrayList<String> myList) {
        for (String entrant : myList) {


            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.entrant_item_layout, entrantListContainer, false);
            TextView userName = itemView.findViewById(R.id.userName);
            ImageView image = itemView.findViewById(R.id.profile_placeholder);
            Map user = new UserDB().getUserMapFromID(entrant,userName,image);


            //expand image


            entrantListContainer.addView(itemView);
            //

        }

    }
}
