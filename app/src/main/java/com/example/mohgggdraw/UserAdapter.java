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
/***
 * Array adapter for waitlist entrants
 * ***/
public class UserAdapter extends ArrayAdapter<String> {

    public UserAdapter(@NonNull Context context, ArrayList<String> waitlist) {
        super(context, 0, waitlist);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Creating the listview
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_waiting_list, parent, false);
        } else {
            view = convertView;
        }

        // Creating the content
        String currentPerson = getItem(position);

        TextView person = view.findViewById(R.id.userName_1); // Use a specific ID from the new XML file
        person.setText(currentPerson);
        return view;
    }
}
