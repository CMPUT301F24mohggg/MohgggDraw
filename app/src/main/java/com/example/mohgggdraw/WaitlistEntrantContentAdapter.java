package com.example.mohgggdraw;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Map;
/***
 * Array adapter that takes the string id of a user and outputs an array adapter user
 * ***/
public class WaitlistEntrantContentAdapter extends ArrayAdapter<String> {
    private SetListView fragment;
    private ArrayList<String> selectedList;

    public WaitlistEntrantContentAdapter(Context context, ArrayList<String> ids, SetListView fragment) {
        super(context, 0, ids );
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.entrant_item_layout_nonclick, parent, false);
        }
        String entrant = getItem(position);
        TextView userName = view.findViewById(R.id.userName);
        ImageView image = view.findViewById(R.id.profile_placeholder);


        new UserDB().getUserMapFromID(entrant, userName, image);
        return view;
    }
}
