package com.example.mohgggdraw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.util.ArrayList;
/***
 * Event adapter for listview
 * takes arraylist of events to display according to event array content
 ***/
public class EventAdapter extends ArrayAdapter<Event> {
    public EventAdapter(Context context, ArrayList events) {
        super(context, 0, events);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup
            parent) {
        //creating the listview
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.event_array_content,
                    parent, false);
        } else {
            view = convertView;
        }
        Event event = getItem(position);
        TextView eventName = view.findViewById(R.id.eventname);

        //sets event listview text
        eventName.setText(event.getTitle());
        ImageView iv = (ImageView) view.findViewById(R.id.eventadapterimage);
        //tries to pull image
        StorageReference myImage = new WaitinglistDB().getImage(event.getPosterUrl());
        try{
            File eventImage = File.createTempFile(event.getTitle(),".png");
            myImage.getFile(eventImage)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(eventImage.getAbsolutePath());
                            iv.setImageBitmap(bitmap);

                        }
                    });{

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return view;
    }

}