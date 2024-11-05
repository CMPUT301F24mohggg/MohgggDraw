package com.example.mohgggdraw;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class BasicInformationFragment extends Fragment {

    private static final int PICK_IMAGE = 1;
    private ImageView organizerEventPoster;
    private EditText eventTitle, eventLocation, eventDetail;
    private OrganizerViewModel organizerViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_basic_information, container, false);

        // Initialize UI elements
        organizerEventPoster = view.findViewById(R.id.organizer_event_poster);
        eventTitle = view.findViewById(R.id.event_title);
        eventLocation = view.findViewById(R.id.event_location);
        eventDetail = view.findViewById(R.id.event_detail);

        // Initialize ViewModel
        organizerViewModel = new ViewModelProvider(requireActivity()).get(OrganizerViewModel.class);

        // Restore data if it exists
        Uri savedImageUri = organizerViewModel.getEventImageUri();
        if (savedImageUri != null) {
            organizerEventPoster.setImageURI(savedImageUri);
        }
        eventTitle.setText(organizerViewModel.getEventTitle());
        eventLocation.setText(organizerViewModel.getEventLocation());
        eventDetail.setText(organizerViewModel.getEventDetail());

        // Set up the image picker
        organizerEventPoster.setOnClickListener(v -> openGallery());

        return view;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            if (selectedImage != null) {
                organizerEventPoster.setImageURI(selectedImage);
                organizerViewModel.setEventImageUri(selectedImage);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Save event details in ViewModel
        organizerViewModel.setEventTitle(eventTitle.getText().toString());
        organizerViewModel.setEventLocation(eventLocation.getText().toString());
        organizerViewModel.setEventDetail(eventDetail.getText().toString());
    }

    public void saveData() {
        // Save the current state of the registration details into the ViewModel
        String title = eventTitle.getText().toString();


        organizerViewModel.setEventTitle(title);
    }
}
