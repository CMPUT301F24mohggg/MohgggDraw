package com.example.mohgggdraw;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class WaitlistViewEntrantsFragment extends Fragment {
    private Event event;
    private final List<Fragment> fragments = new ArrayList<>();

    public WaitlistViewEntrantsFragment() {
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewPager2
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);

        // Initialize tabs
        TextView tab1 = view.findViewById(R.id.tab1);
        TextView tab2 = view.findViewById(R.id.tab2);
        TextView tab3 = view.findViewById(R.id.tab3);
        TextView tab4 = view.findViewById(R.id.tab4);

        // Create fragments for each tab
        fragments.add(new WaitlistEntrantContentFragment()); // First tab
        fragments.add(new WaitlistEntrantContentCancelledFragment()); // Second tab
        fragments.add(new WaitlistEntrantContentSelectedFragment()); // Third tab
        fragments.add(new WaitlistEntrantContentConfirmedFragment()); // Fourth tab

        // Set event to fragments
        if (event != null) {
            for (Fragment fragment : fragments) {
                if (fragment instanceof WaitlistEntrantContentFragment) {
                    ((WaitlistEntrantContentFragment) fragment).setEvent(event);
                }
            }
        }

        // Set up ViewPager adapter
        TabFragmentAdapter adapter = new TabFragmentAdapter(this, fragments);
        viewPager.setAdapter(adapter);

        // Tab click listeners
        tab1.setOnClickListener(v -> viewPager.setCurrentItem(0));
        tab2.setOnClickListener(v -> viewPager.setCurrentItem(1));
        tab3.setOnClickListener(v -> viewPager.setCurrentItem(2));
        tab4.setOnClickListener(v -> viewPager.setCurrentItem(3));

        // ViewPager page change listener
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateTabStyles(tab1, tab2, tab3, tab4, position);
            }
        });

        // Set default tab style
        updateTabStyles(tab1, tab2, tab3, tab4, 0);
    }

    private void updateTabStyles(TextView tab1, TextView tab2, TextView tab3, TextView tab4, int position) {
        // Reset all tab styles
        tab1.setBackgroundResource(R.drawable.tab_unselected_background);
        tab2.setBackgroundResource(R.drawable.tab_unselected_background);
        tab3.setBackgroundResource(R.drawable.tab_unselected_background);
        tab4.setBackgroundResource(R.drawable.tab_unselected_background);

        tab1.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        tab2.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        tab3.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
        tab4.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));

        // Highlight the selected tab
        switch (position) {
            case 0:
                tab1.setBackgroundResource(R.drawable.tab_selected_background);
                tab1.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_500));
                break;
            case 1:
                tab2.setBackgroundResource(R.drawable.tab_selected_background);
                tab2.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_500));
                break;
            case 2:
                tab3.setBackgroundResource(R.drawable.tab_selected_background);
                tab3.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_500));
                break;
            case 3:
                tab4.setBackgroundResource(R.drawable.tab_selected_background);
                tab4.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_500));
                break;
        }
    }

    public void updateFragments() {
        if (fragments.isEmpty()) return;

        for (Fragment fragment : fragments) {
            if (fragment instanceof WaitlistEntrantContentFragment) {
                ((WaitlistEntrantContentFragment) fragment).refreshAdapter();
            } else if (fragment instanceof WaitlistEntrantContentSelectedFragment) {
                ((WaitlistEntrantContentSelectedFragment) fragment).refreshAdapter();
            }
        }
    }
}
