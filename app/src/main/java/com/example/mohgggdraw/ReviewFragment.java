package com.example.mohgggdraw;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide; // Import Glide for image loading

public class ReviewFragment extends Fragment {

    private SharedViewModel sharedViewModel;
    private TextView reviewEventTitle, reviewEventLocation;
    private TextView reviewRegistrationOpen, reviewRegistrationDeadline, reviewEventStartTime;
    private TextView reviewMaxPoolingSample, reviewMaxEntrants, reviewGeolocationEnabled;
    private ImageView reviewEventBanner; // Add this line

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review, container, false);

        // Initialize TextViews
        reviewEventTitle = view.findViewById(R.id.review_event_title);
        reviewEventLocation = view.findViewById(R.id.review_event_location);
        reviewRegistrationOpen = view.findViewById(R.id.review_registration_open);
        reviewRegistrationDeadline = view.findViewById(R.id.review_registration_deadline);
        reviewMaxPoolingSample = view.findViewById(R.id.review_max_pooling_sample);
        reviewMaxEntrants = view.findViewById(R.id.review_max_entrants);
        reviewGeolocationEnabled = view.findViewById(R.id.review_geolocation);
        reviewEventBanner = view.findViewById(R.id.organizer_event_poster); // Initialize the ImageView

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Observe LiveData from ViewModel
        sharedViewModel.getEventTitle().observe(getViewLifecycleOwner(), title -> {
            if (reviewEventTitle != null) {
                reviewEventTitle.setText(title);
            } else {
                Log.e("ReviewFragment", "reviewEventTitle is null");
            }
        });

        sharedViewModel.getEventLocation().observe(getViewLifecycleOwner(), location -> {
            if (reviewEventLocation != null) {
                reviewEventLocation.setText(location);
            } else {
                Log.e("ReviewFragment", "reviewEventLocation is null");
            }
        });

        // Observe the geolocation status and update TextView
        sharedViewModel.getEnableGeolocation().observe(getViewLifecycleOwner(), enabled -> {
            if (reviewGeolocationEnabled != null) {
                reviewGeolocationEnabled.setText(enabled ? "Yes" : "No");
            } else {
                Log.e("ReviewFragment", "reviewGeolocationEnabled is null");
            }
        });

        // Observe the image URL and set it in the ImageView
        sharedViewModel.getImageUrl().observe(getViewLifecycleOwner(), url -> {
            if (reviewEventBanner != null && url != null) { // Check if URL is not null
                Glide.with(requireContext()) // Use requireContext() for valid context
                        .load(url) // Load the image from the URL
                        .into(reviewEventBanner); // Set the image in the ImageView
            } else {
                Log.e("ReviewFragment", "reviewEventBanner is null or URL is null");
            }
        });


        return view;
    }
}
