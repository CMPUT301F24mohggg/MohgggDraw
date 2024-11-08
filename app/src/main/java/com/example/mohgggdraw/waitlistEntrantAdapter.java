package com.example.mohgggdraw;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class waitlistEntrantAdapter extends ArrayAdapter<String> {

    public waitlistEntrantAdapter(@NonNull Context context, ArrayList<String> waitlist) {
        super(context, 0, waitlist);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup
            parent) {
        //creating the listview
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.waitlist_person,
                    parent, false);
        } else {
            view = convertView;
        }

        TextView person = view.findViewById(R.id.waitlistPersonName);
        person.setText(getItem(position));
        return view;
    }
}
