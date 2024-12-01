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
import java.util.ArrayList;

public class EventAdapter extends ArrayAdapter<Event> {
    private Context mContext;

    public EventAdapter(@NonNull Context context, @NonNull ArrayList<Event> events) {
        super(context, 0, events);
        this.mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.event_array_content, parent, false);
            holder = new ViewHolder();
            holder.eventName = view.findViewById(R.id.eventname);
            holder.eventImage = view.findViewById(R.id.eventadapterimage);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Event event = getItem(position);
        if (event != null) {
            holder.eventName.setText(event.getTitle());
            loadEventImage(event, holder.eventImage);
        }

        return view;
    }

    private void loadEventImage(Event event, ImageView imageView) {
        StorageReference myImage = new WaitinglistDB().getImage(event.getPosterUrl());
        try {
            File eventImage = File.createTempFile(event.getTitle(), ".png");
            myImage.getFile(eventImage)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(eventImage.getAbsolutePath());
                            imageView.setImageBitmap(bitmap);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("EventAdapter", "Error loading image", e);
                        // Optionally set a default image
                        imageView.setImageResource(R.drawable.img_wo_logo);
                    });
        } catch (IOException e) {
            Log.e("EventAdapter", "Error creating temp file", e);
            imageView.setImageResource(R.drawable.img_wo_logo);
        }
    }

    // ViewHolder pattern to improve performance
    private static class ViewHolder {
        TextView eventName;
        ImageView eventImage;
    }
}