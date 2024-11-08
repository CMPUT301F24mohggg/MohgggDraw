package com.example.mohgggdraw;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileOverviewFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button buttonEditProfile = view.findViewById(R.id.buttonEditProfile);
        Button buttonLogout = view.findViewById(R.id.buttonLogout);
        Switch switchNotifications = view.findViewById(R.id.switchNotifications);
        Button buttonBackToOverview = view.findViewById(R.id.buttonBackToOverview);

        // Set onClickListener for Edit Profile button to navigate to ProfileFragment
        buttonEditProfile.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ProfileFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Set onClickListener for Logout button
        buttonLogout.setOnClickListener(v -> {
            // Log out logic (could be FirebaseAuth signOut() or clearing session data)
            // For now, simply show a placeholder message
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        // Set onClickListener for Back to Overview button
        buttonBackToOverview.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }

}
