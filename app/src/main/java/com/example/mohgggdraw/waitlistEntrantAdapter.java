package com.example.mohgggdraw;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
/***
 * Array adapter for waitlist entrants
 * ***/
public class waitlistEntrantAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> entrants;
    private final ArrayList<String> selectedEntrants = new ArrayList<>();

    public waitlistEntrantAdapter(@NonNull Context context, ArrayList<String> entrants) {
        super(context, 0, entrants);
        this.context = context;
        this.entrants = entrants;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = (convertView == null)
                ? LayoutInflater.from(context).inflate(R.layout.entrant_item_layout, parent, false)
                : convertView;

        String entrant = entrants.get(position);
        TextView userName = view.findViewById(R.id.userName);
        CheckBox selectCheckBox = view.findViewById(R.id.selectCheckBox);

        userName.setText(entrant);

        // Maintain selection state
        selectCheckBox.setOnCheckedChangeListener(null);
        selectCheckBox.setChecked(selectedEntrants.contains(entrant));
        selectCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedEntrants.contains(entrant)) {
                    selectedEntrants.add(entrant);
                }
            } else {
                selectedEntrants.remove(entrant);
            }
        });

        return view;
    }

    public ArrayList<String> getSelectedEntrants() {
        return selectedEntrants;
    }
}
