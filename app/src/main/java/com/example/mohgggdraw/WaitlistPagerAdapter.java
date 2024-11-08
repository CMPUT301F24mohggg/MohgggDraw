package com.example.mohgggdraw;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class WaitlistPagerAdapter extends FragmentStateAdapter {
    Event event;
    User user;
    Fragment fragment;

    public WaitlistPagerAdapter(@NonNull Fragment fragment, User user) {
        super(fragment.getChildFragmentManager(), fragment.getLifecycle());

        this.user =user;
        this.fragment = fragment;

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch(position) {

            case 1:
                return new WaitlistFragment(event,user,(HomeFragment) fragment);
            case 2:
                return new WaitlistViewEntrantsFragment(event);
            default:
                return new EventListDisplayFragment(user, (HomeFragment) fragment);
        }
//        if(position == 1){
//            return new WaitlistFragment(event,user,(HomeFragment) fragment);
//        }
//        else{
//
//            return new EventListDisplayFragment(user, (HomeFragment) fragment);
//        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
    public void setEvent(Event event){
        this.event = event;
    }
}
