package com.example.mohgggdraw;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/***
 This fragment represents the home screen of the application. It:
 - Inflates the home screen layout
 - Sets up any necessary UI components or listeners
 ***/
public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set up your UI components or add listeners here if needed
        // For example, finding your TextView:
        // TextView sampleText = view.findViewById(R.id.HomeFragmentSample);
    }
}
