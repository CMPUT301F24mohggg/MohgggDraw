package com.example.mohgggdraw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/***
 * Event adapter for listview
 * takes arraylist of events to display according to event array content
 ***/
public class EventAdapter extends ArrayAdapter<Event> {

    public EventAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Creating or reusing the list view item
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.event_array_content, parent, false);
        }

        // Get the current event
        Event event = getItem(position);
        if (event == null) {
            return view;
        }



        // Find views from XML layout
        TextView eventTitle = view.findViewById(R.id.eventTitle);
        TextView eventDate = view.findViewById(R.id.eventDate);
        TextView eventDetails = view.findViewById(R.id.eventDetails);
        TextView eventDescription = view.findViewById(R.id.eventDescription);
        ImageView eventImage = view.findViewById(R.id.eventImage);
        TextView title = view.findViewById(R.id.registeredTitle);

        if(event.getFlag()!=-1){
            if(event.getFlag() == 1){
                title.setText("Waiting");

            } else if (event.getFlag()==2) {
                title.setText("Registered");

            } else if (event.getFlag() ==0) {
                title.setText("Created Event");

            }

        }

        // Set event title
        eventTitle.setText(event.getTitle() != null ? event.getTitle() : "Untitled Event");

        // Set event date (Month and Day)

        if(event.getStartTime() != null){


        }
        eventDate.setText(event.getStartTime() != null ? new SimpleDateFormat("MMMM").format(event.getStartTime()).substring(0,3) +"\n"+ String.valueOf(event.getStartTime().getDate()): "Unknown Date");

        // Set event time details
        String time="";
        if( event.getStartTime()!=null) {
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTime(event.getStartTime());
            time = (cal.get(Calendar.DAY_OF_WEEK))+" "+String.valueOf(cal.get(Calendar.HOUR)==0?"12":cal.get(Calendar.HOUR))+":"+cal.get(Calendar.MINUTE)+(cal.get(Calendar.AM_PM)==0?"AM":"PM");

        }
        String timeDetails = String.format("Time: %s", event.getStartTime() != null ? time : "Unknown Time");
        eventDetails.setText(timeDetails);

        // Set event description
        eventDescription.setText(event.getRegistrationDetails() != null ? event.getRegistrationDetails() : "No Details Available");

        // Set event image from Firebase Storage
        if (event.getPosterUrl() != null) {
            try {
                File eventImageFile = File.createTempFile(
                        event.getEventId(), // Handle null titles
                        ".png"
                );
                StorageReference myImage = new WaitinglistDB().getImage(event.getPosterUrl());
                myImage.getFile(eventImageFile)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Bitmap bitmap = BitmapFactory.decodeFile(eventImageFile.getAbsolutePath());
                                eventImage.setImageBitmap(bitmap);
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
                // Optionally set a placeholder image on error
                eventImage.setImageResource(R.drawable.eventpage_banner_placeholder);
            }
        } else {
            // Set a placeholder image if no URL is provided
            eventImage.setImageResource(R.drawable.eventpage_banner_placeholder);
        }

        return view;
    }
}
