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

/***
 * Content fragment to display.
 */
public class WaitlistEntrantContentSelectedFragment extends Fragment implements SetListView{
    private ArrayList<String> dataList;
    private Event event;
    private LinearLayout entrantListContainer;
   boolean viewCreated=false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_waiting_list_selected, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //dataList = event.getWaitingList();
        entrantListContainer = view.findViewById(R.id.listContainer);

        viewCreated = true;
        if(event!=null) {
            new WaitinglistDB().setListFromDB("EventSelectedlist", this, event);
        }



        // Populate entrant list dynamically

    }
    public void setEvent(Event event){
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