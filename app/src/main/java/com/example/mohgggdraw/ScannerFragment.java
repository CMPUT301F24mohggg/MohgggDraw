package com.example.mohgggdraw;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.firestore.CollectionReference;

/***
 * This fragment manages the scanning process for event QR codes.
 * - Sets up a ViewPager2 to navigate between different creation steps.
 * - Handles navigation between steps (back buttons).
 * - Updates UI elements based on the current step.
 */
public class ScannerFragment extends Fragment {
    private String eventId;
    private WaitinglistDB waitinglistDB = new WaitinglistDB();
    private CollectionReference eventRef = waitinglistDB.getWaitlistRef();
    private ViewPager2 viewPager2;
    private ScannerViewModel scannerViewModel;
    private ImageView backButton;
    private ViewPager2.OnPageChangeCallback pageChangeCallback;

    /**
     * Inflates the layout for this fragment.
     *
     * @param inflater LayoutInflater to inflate the views in this fragment.
     * @param container The parent view to attach this fragment to.
     * @param savedInstanceState The previously saved state of the fragment, if any.
     * @return The root view of the fragment.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        scannerViewModel = new ViewModelProvider(requireActivity()).get(ScannerViewModel.class);
        return inflater.inflate(R.layout.fragment_scanner, container, false);
    }

    /**
     * Sets up the fragment's UI elements and ViewPager2 after the view is created.
     *
     * @param view The root view of the fragment.
     * @param savedInstanceState The previously saved state of the fragment, if any.
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backButton = view.findViewById(R.id.scanner_back_button);

        // Set up adapter for ViewPager2
        viewPager2 = view.findViewById(R.id.scanner_viewpager);
        ScannerPagerAdapter adapter = new ScannerPagerAdapter(this);
        viewPager2.setAdapter(adapter);

        // Handle page changes
        viewPager2.registerOnPageChangeCallback(pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    scannerViewModel.setScanStatus(1);
                }
                updateUIForPosition(position);
            }
        });

        backButton.setOnClickListener(v -> {
            swapToFragment(0);
        });

        // Initialize to camera fragment
        swapToFragment(0);
    }

    /**
     * Unregisters the ViewPager2 page change callback to prevent memory leaks.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        viewPager2.unregisterOnPageChangeCallback(pageChangeCallback);
    }

    /**
     * Switches to the specified fragment in the ViewPager2.
     *
     * @param position The index of the fragment to display.
     */
    public void swapToFragment(int position) {
        if (viewPager2 != null) {
            viewPager2.setCurrentItem(position);
        }
    }

    /**
     * Updates the UI based on the current fragment's position.
     *
     * @param position The index of the current fragment.
     */
    private void updateUIForPosition(int position) {
        boolean isBackButtonVisible = position != 0;
        backButton.setVisibility(isBackButtonVisible ? View.VISIBLE : View.INVISIBLE);
    }
}