package com.example.mohgggdraw;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.firestore.CollectionReference;


/***
 This fragment manages the scanning process for event QR codes. It:
 - Sets up a ViewPager2 to navigate between different creation steps
 - Handles navigation between steps (back buttons)
 - Updates UI elements
 ***/
public class BrowseProfiles extends Fragment {
    private String eventId;
    private WaitinglistDB waitinglistDB = new WaitinglistDB();
    private CollectionReference eventRef = waitinglistDB.getWaitlistRef();
    private ViewPager2 viewPager2;
    private ScannerViewModel scannerViewModel;
    private ImageView backButton;
    private ViewPager2.OnPageChangeCallback pageChangeCallback;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        scannerViewModel = new ViewModelProvider(requireActivity()).get(ScannerViewModel.class);
        return inflater.inflate(R.layout.fragment_browse_profiles, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        backButton = view.findViewById(R.id.scanner_back_button);


        // Set up adapter for ViewPager2
        viewPager2 = view.findViewById(R.id.scanner_viewpager);
        ScannerPagerAdapter adapter = new ScannerPagerAdapter(this);
        viewPager2.setAdapter(adapter);



        // Whenever page changes to camera fragment set to start scanning
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


        // Swap to camera fragment
        swapToFragment(0);

    }

    // Unregister to prevent memory leaks
    @Override
    public void onDestroy() {
        super.onDestroy();
        viewPager2.unregisterOnPageChangeCallback(pageChangeCallback);
    }

    public void swapToFragment(int position) {
        if (viewPager2 != null) {
            viewPager2.setCurrentItem(position);
        }
    }

    private void updateUIForPosition(int position) {
        boolean isBackButtonVisible = true;

        switch (position) {
            case 0:
                isBackButtonVisible = false;
                break;
            case 1:
                isBackButtonVisible = true;
                break;
            case 2:
                isBackButtonVisible = true;
                break;
            default:
                break;
        }
        backButton.setVisibility(isBackButtonVisible ? View.VISIBLE : View.INVISIBLE);
    }

}



