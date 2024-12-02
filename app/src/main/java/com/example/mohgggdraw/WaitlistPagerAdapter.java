package com.example.mohgggdraw;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/***
 * waitlist pager adapter to move between the waitlist homepages
 * ***/

public class WaitlistPagerAdapter extends FragmentStateAdapter {
    Event event;
    User user;
    Fragment fragment;
    boolean adminView = false;

    public WaitlistPagerAdapter(@NonNull Fragment fragment, User user) {
        super(fragment.getChildFragmentManager(), fragment.getLifecycle());

        this.user =user;
        this.fragment = fragment;

    }

    //different pages in hometab
    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch(position) {

            case 1:
                WaitlistFragment waitlistFragment = new WaitlistFragment();
                waitlistFragment.setImportant(event,(HomeFragment)fragment);
                return waitlistFragment;
            case 2:
                WaitlistViewEntrantsFragment waitlistViewEntrantsFragment = new WaitlistViewEntrantsFragment();
                waitlistViewEntrantsFragment.setEvent(event);
                waitlistViewEntrantsFragment.setHome((HomeFragment) fragment);
                return waitlistViewEntrantsFragment;

            case 3:
                MapFragment map = new MapFragment();
                return map;


            default:
                if (adminView) {
                    AdminEventView adminEventView = new AdminEventView();
                    adminEventView.setFragment((HomeFragment) fragment);
                    return adminEventView;

                }
                else{

                    EventListTabViewFragment fragment1 = new EventListTabViewFragment();
                    fragment1.setHomeFragment((HomeFragment) fragment);
                    return fragment1;

                }
        }
//
    }

    @Override
    public int getItemCount() {
        return 3;
    }
    public void setEvent(Event event){
        this.event = event;
    }

    public void setAdminView() {
        adminView = true;
    }
}
