package com.example.mohgggdraw;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
/***
 This adapter manages the fragments used in the event creation process. It:
 - Creates and returns the appropriate fragment for each position
 - Determines the total number of creation steps
 - Handles fragment identification and containment checks
 ***/
public class CreatePagerAdapter extends FragmentStateAdapter {

    public CreatePagerAdapter(@NonNull Fragment fragment) {
        super(fragment.getChildFragmentManager(), fragment.getLifecycle());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new BasicInformationFragment();
            case 1:
                return new RegistrationDetailsFragment();
            case 2:
                return new ParticipationSettingsFragment();
            case 3:
                return new ReviewFragment();
            case 4:
                return new QrCreatedFragment();
            case 5:
                return new QrWaitlistFragment(1);
            default:
                return new EventCreatedSuccessFragment();

        }
    }

    @Override
    public int getItemCount() {
        return 7; // Total number of pages
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
