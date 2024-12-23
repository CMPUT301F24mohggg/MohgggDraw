package com.example.mohgggdraw;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
/***
 This fragment displays the user's events. It:
 - Inflates the layout for displaying user events
 - Sets up any necessary UI components or listeners
 ***/
public class MyEventsFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_myevents, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set up your UI components or add listeners here if needed
        // For example, finding your TextView:
        // TextView sampleText = view.findViewById(R.id.HomeFragmentSample);
    }
}
