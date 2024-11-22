package com.example.mohgggdraw;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
/***
 This adapter manages the fragments used in the event creation process. It:
 - Creates and returns the appropriate fragment for each position
 - Determines the total number of creation steps
 - Handles fragment identification and containment checks
 ***/
public class ScannerPagerAdapter extends FragmentStateAdapter {

    public ScannerPagerAdapter(@NonNull Fragment fragment) {
        super(fragment.getChildFragmentManager(), fragment.getLifecycle());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ScannerCameraFragment();
            case 1:
                return new ScannerInvalidFragment();
            case 2:
                return new QrWaitlistFragment(0);
            default:
                return new ScannerCameraFragment();
        }
    }



    @Override
    public int getItemCount() {
        return 4; // Total number of pages
    }

    @Override
    public long getItemId(int position) {
        return position; // Unique ID for each fragment position
    }

    @Override
    public boolean containsItem(long itemId) {
        return itemId < getItemCount();
    }
}
