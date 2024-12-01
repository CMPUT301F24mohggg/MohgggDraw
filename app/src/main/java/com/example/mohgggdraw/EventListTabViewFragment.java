package com.example.mohgggdraw;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class EventListTabViewFragment extends Fragment {
    List<Fragment> fragments= new ArrayList<>();
    String deviceID;
    HomeFragment home;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize TabLayout and ViewPager2
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);
        deviceID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Create fragments for tabs
        fragments.add(new EventListDisplayFragment()); // First tab waitlist
        fragments.add(new EventListConfirmFragment()); //selected
        fragments.add(new EventListCreatedFragment());//cancelled

        ((EventListView)fragments.get(0)).setDevice(deviceID);
        ((EventListView)fragments.get(0)).setFragment(home);
        ((EventListView)fragments.get(1)).setDevice(deviceID);
        ((EventListView)fragments.get(2)).setDevice(deviceID);
        ((EventListView)fragments.get(1)).setFragment(home);
        ((EventListView)fragments.get(2)).setFragment(home);




        // Tab titles
        List<String> tabTitles = new ArrayList<>();
        tabTitles.add("My Events");
        tabTitles.add("Waiting List");
        tabTitles.add("Created");


        // Set up adapter
        TabFragmentAdapter adapter = new TabFragmentAdapter(this, fragments);
        viewPager.setAdapter(adapter);

        // Link TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(tabTitles.get(position))).attach();
    }

    public void setHomeFragment(HomeFragment home){
        this.home = home;

    }









}
