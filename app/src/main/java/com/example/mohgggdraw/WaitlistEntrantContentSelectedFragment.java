package com.example.mohgggdraw;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/***
 * Content fragment to display entrants for the first tab.
 */
public class WaitlistEntrantContentSelectedFragment extends Fragment implements SetListView{
    private ArrayList<String> dataList;
    private Event event;
    private LinearLayout entrantListContainer;

    public WaitlistEntrantContentSelectedFragment(Event event) {
        this.event = event;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_waiting_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //dataList = event.getWaitingList();
        entrantListContainer = view.findViewById(R.id.listContainer);
        new WaitinglistDB().setListFromDB("EventConfirmedlist",this,event);



        // Populate entrant list dynamically

    }
    public void startList(Event event){
        


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
