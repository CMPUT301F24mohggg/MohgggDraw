package com.example.mohgggdraw;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * BrowseProfilesPagerAdapter is an adapter class for managing fragments in the BrowseProfilesFragment.
 * <p>
 * It handles the creation and lifecycle of fragments displayed in a ViewPager2, specifically:
 * <ul>
 * <li>BrowseFacilitiesFragment</li>
 * <li>BrowseUsersFragment</li>
 * <li>BrowseImagesFragment</li>
 * <li>AdminEventView</li>
 * </ul>
 */
public class BrowseProfilesPagerAdapter extends FragmentStateAdapter {

    /**
     * Constructs the adapter with the child fragment manager and lifecycle.
     *
     * @param fragment The parent fragment hosting the ViewPager2.
     */
    public BrowseProfilesPagerAdapter(@NonNull Fragment fragment) {
        super(fragment.getChildFragmentManager(), fragment.getLifecycle());
    }

    /**
     * Creates a new fragment based on the given position.
     * <p>
     * Position to Fragment mapping:
     * <ul>
     * <li>0: BrowseFacilitiesFragment</li>
     * <li>1: BrowseUsersFragment</li>
     * <li>2: BrowseImagesFragment</li>
     * <li>3: AdminEventView</li>
     * </ul>
     *
     * @param position The position of the fragment in the ViewPager2.
     * @return A new instance of the corresponding fragment.
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new BrowseFacilitiesFragment();
            case 1:
                return new BrowseUsersFragment();
            case 2:
                return new BrowseImagesFragment();
            case 3:
                return new AdminEventView(); // New Events fragment
            default:
                return new BrowseFacilitiesFragment(); // Default fallback
        }
    }

    /**
     * Returns the total number of fragments managed by the adapter.
     *
     * @return The total number of fragments (4: Facilities, Users, Images, Events).
     */
    @Override
    public int getItemCount() {
        return 4; // Updated to include the new "Events" tab
    }

    /**
     * Returns a unique ID for each fragment based on its position.
     *
     * @param position The position of the fragment in the ViewPager2.
     * @return A unique ID for the fragment.
     */
    @Override
    public long getItemId(int position) {
        return position; // Unique ID for each fragment position
    }

    /**
     * Checks if a fragment with the given ID is currently managed by the adapter.
     *
     * @param itemId The unique ID of the fragment.
     * @return True if the fragment is managed by the adapter, false otherwise.
     */
    @Override
    public boolean containsItem(long itemId) {
        return itemId < getItemCount();
    }
}
