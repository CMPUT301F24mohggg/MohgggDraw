package com.example.mohgggdraw;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class EventListTabViewFragment extends Fragment {
    List<Fragment> fragments = new ArrayList<>();
    String deviceID;
    HomeFragment home;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get device ID
        deviceID = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Initialize custom tab bar and ViewPager2
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);
        List<TextView> tabs = new ArrayList<>();
        tabs.add(view.findViewById(R.id.tab1)); // My Events
        tabs.add(view.findViewById(R.id.tab2)); // Waiting List
        tabs.add(view.findViewById(R.id.tab3)); // Created

        // Create fragments for each tab
        fragments.add(new EventListDisplayFragment()); // My Events
        fragments.add(new EventListConfirmFragment()); // Waiting List
        fragments.add(new EventListCreatedFragment()); // Created

        // Set device ID and home fragment in each custom fragment
        for (int i = 0; i < fragments.size(); i++) {
            ((EventListView) fragments.get(i)).setDevice(deviceID);
            ((EventListView) fragments.get(i)).setFragment(home);
        }

        // Set up ViewPager2 adapter
        TabFragmentAdapter adapter = new TabFragmentAdapter(this, fragments);
        viewPager.setAdapter(adapter);

        // Handle tab clicks
        for (int i = 0; i < tabs.size(); i++) {
            int finalI = i;
            tabs.get(i).setOnClickListener(v -> {
                viewPager.setCurrentItem(finalI); // Switch ViewPager page
                updateTabStyles(tabs, finalI);   // Update tab styles
            });
        }

        // Sync tab styles with ViewPager page changes
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateTabStyles(tabs, position); // Update styles based on page
            }
        });
    }

    private void updateTabStyles(List<TextView> tabs, int selectedIndex) {
        for (int i = 0; i < tabs.size(); i++) {
            TextView tab = tabs.get(i);
            if (i == selectedIndex) {
                tab.setTextColor(getResources().getColor(R.color.selected_text_color)); // Use selected text color
                tab.setBackgroundResource(R.drawable.tab_selected_background);         // Use selected background
            } else {
                tab.setTextColor(getResources().getColor(R.color.unselected_text_color)); // Use unselected text color
                tab.setBackgroundResource(R.drawable.tab_unselected_background);         // Use unselected background
            }
        }
    }

    public void setHomeFragment(HomeFragment home) {
        this.home = home;
    }
}
