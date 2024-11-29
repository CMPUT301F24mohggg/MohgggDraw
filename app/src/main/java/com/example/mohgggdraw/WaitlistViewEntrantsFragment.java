package com.example.mohgggdraw;

import android.os.Bundle;
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

/***
 * Main fragment to handle TabLayout and ViewPager2 for entrants.
 */
public class WaitlistViewEntrantsFragment extends Fragment {
    private Event event;

    public WaitlistViewEntrantsFragment() {

    }

    public void setEvent(Event event) {
        this.event = event;
    }

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

        // Create fragments for tabs
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new WaitlistEntrantContentFragment(event)); // First tab waitlist
        fragments.add(new WaitlistEntrantContentSelectedFragment()); //selected
        fragments.add(new WaitlistEntrantContentCancelledFragment(event)); //cancelled
        fragments.add(new WaitlistEntrantContentConfirmedFragment(event));//confirmed

        ((WaitlistEntrantContentSelectedFragment)fragments.get(1)).startList(event);


        // Tab titles
        List<String> tabTitles = new ArrayList<>();
        tabTitles.add("Waiting");
        tabTitles.add("Selected");
        tabTitles.add("Cancelled");
        tabTitles.add("Confirmed");

        // Set up adapter
        TabFragmentAdapter adapter = new TabFragmentAdapter(this, fragments);
        viewPager.setAdapter(adapter);

        // Link TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(tabTitles.get(position))).attach();
    }
}
