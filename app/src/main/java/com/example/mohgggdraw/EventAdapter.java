package com.example.mohgggdraw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/***
 * Event adapter for listview
 * takes arraylist of events to display according to event array content
 ***/
public class EventAdapter extends ArrayAdapter<Event> {

    public EventAdapter(@NonNull Context context, @NonNull ArrayList<Event> events) {
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

        // Set event title
        eventTitle.setText(event.getTitle() != null ? event.getTitle() : "Untitled Event");

        // Set event date (Month and Day)
        eventDate.setText(event.getStartTime() != null ?
                new SimpleDateFormat("MMM\nd", Locale.getDefault()).format(event.getStartTime()) :
                "Unknown Date");

        // Set event time details
        String time = "";
        if (event.getStartTime() != null) {
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTime(event.getStartTime());

            int hour = cal.get(Calendar.HOUR);
            hour = hour == 0 ? 12 : hour;
            String minuteStr = String.format("%02d", cal.get(Calendar.MINUTE));
            String amPm = cal.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM";

            time = String.format("%s %s:%s %s",
                    getDayOfWeekString(cal.get(Calendar.DAY_OF_WEEK)),
                    hour,
                    minuteStr,
                    amPm);
        }

        String timeDetails = String.format("Time: %s",
                event.getStartTime() != null ? time : "Unknown Time");
        eventDetails.setText(timeDetails);

        // Set event description
        eventDescription.setText(event.getRegistrationDetails() != null ?
                event.getRegistrationDetails() :
                "No Details Available");

        // Set event image from Firebase Storage
        loadEventImage(event, eventImage);

        return view;
    }

    /**
     *loads image from event
     * */

    private void loadEventImage(Event event, ImageView imageView) {
        if (event.getPosterUrl() != null) {
            try {
                File eventImageFile = File.createTempFile(
                        event.getEventId() != null ? event.getEventId() : "event",
                        ".png"
                );
                StorageReference myImage = new WaitinglistDB().getImage(event.getPosterUrl());
                myImage.getFile(eventImageFile)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Bitmap bitmap = BitmapFactory.decodeFile(eventImageFile.getAbsolutePath());
                                imageView.setImageBitmap(bitmap);
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("EventAdapter", "Error loading image", e);
                            imageView.setImageResource(R.drawable.eventpage_banner_placeholder);
                        });
            } catch (IOException e) {
                Log.e("EventAdapter", "Error creating temp file", e);
                imageView.setImageResource(R.drawable.eventpage_banner_placeholder);
            }
        } else {
            // Set a placeholder image if no URL is provided
            imageView.setImageResource(R.drawable.eventpage_banner_placeholder);
        }
    }

    // Helper method to convert day of week to string
    private String getDayOfWeekString(int dayOfWeek) {
        switch (dayOfWeek) {
            case Calendar.SUNDAY: return "Sun";
            case Calendar.MONDAY: return "Mon";
            case Calendar.TUESDAY: return "Tue";
            case Calendar.WEDNESDAY: return "Wed";
            case Calendar.THURSDAY: return "Thu";
            case Calendar.FRIDAY: return "Fri";
            case Calendar.SATURDAY: return "Sat";
            default: return "";
        }
    }
}