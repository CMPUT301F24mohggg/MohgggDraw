package com.example.mohgggdraw;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/***
 * Waitlist pager adapter to move between the waitlist homepages
 * ***/
public class WaitlistPagerAdapter extends FragmentStateAdapter {
    private Event event;
    private User user;
    private Fragment fragment;
    private boolean adminView = false;

    // New fragments for notification flow
    private NotificationDetailsFragment notificationDetailsFragment;

    public WaitlistPagerAdapter(@NonNull Fragment fragment, User user) {
        super(fragment.getChildFragmentManager(), fragment.getLifecycle());
        this.user = user;
        this.fragment = fragment;
    }

    // Different pages in home tab
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
            case 1:
                // Waitlist Fragment with flexible initialization
                WaitlistFragment waitlistFragment = new WaitlistFragment();
                waitlistFragment.setImportant(event, (HomeFragment)fragment);
                return waitlistFragment;

            case 2:
                // Waitlist Entrants Fragment with flexible initialization
                WaitlistViewEntrantsFragment waitlistViewEntrantsFragment = new WaitlistViewEntrantsFragment();
                waitlistViewEntrantsFragment.setEvent(event);
                waitlistViewEntrantsFragment.setHome((HomeFragment) fragment);
                return waitlistViewEntrantsFragment;

            case 3:
                // List Selection or Map Fragment
                if (fragment instanceof ListSelectionFragment.ListSelectionListener) {
                    // Retain List Selection functionality
                    return new ListSelectionFragment(event, (ListSelectionFragment.ListSelectionListener) fragment);
                } else {
                    // Alternative fragment (Map in this case)
                    return new MapFragment();
                }

            case 4:
                // Notification Details Fragment
                if (notificationDetailsFragment != null) {
                    return notificationDetailsFragment;
                }

            default:
                    EventListTabViewFragment fragment1 = new EventListTabViewFragment();
                    fragment1.setHomeFragment((HomeFragment) fragment);
                    return fragment1;
        }
//
    }

    @Override
    public int getItemCount() {
        // Adjust item count based on added fragments
        return adminView ? 3 : 5;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public void setAdminView() {
        this.adminView = true;
    }

    public void setNotificationDetailsFragment(NotificationDetailsFragment fragment) {
        this.notificationDetailsFragment = fragment;
    }
}