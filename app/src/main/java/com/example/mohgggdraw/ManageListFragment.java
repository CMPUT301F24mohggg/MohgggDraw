package com.example.mohgggdraw;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class ManageListFragment extends Fragment {

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

        // Custom Tab Bar (LinearLayout)
        List<TextView> tabs = new ArrayList<>();
        tabs.add(view.findViewById(R.id.tab1)); // Waiting List
        tabs.add(view.findViewById(R.id.tab2)); // Cancelled List
        tabs.add(view.findViewById(R.id.tab3)); // Selected List
        tabs.add(view.findViewById(R.id.tab4)); // Entrant List

        // Create fragments for each tab
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new WaitlistEntrantContentFragment());
        fragments.add(new WaitlistEntrantContentCancelledFragment());
        fragments.add(new WaitlistEntrantContentSelectedFragment());
        fragments.add(new WaitlistEntrantContentConfirmedFragment());

        // Set up ViewPager2 adapter
        TabFragmentAdapter adapter = new TabFragmentAdapter(this, fragments);
        viewPager.setAdapter(adapter);

        // Handle tab clicks
        for (int i = 0; i < tabs.size(); i++) {
            int finalI = i;
            tabs.get(i).setOnClickListener(v -> {
                viewPager.setCurrentItem(finalI);
                updateTabStyles(tabs, finalI);
            });
        }

        // Sync tab styles with ViewPager page changes
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateTabStyles(tabs, position);
            }
        });
    }

    private void updateTabStyles(List<TextView> tabs, int selectedIndex) {
        for (int i = 0; i < tabs.size(); i++) {
            TextView tab = tabs.get(i);
            if (i == selectedIndex) {
                tab.setTextColor(getResources().getColor(R.color.purple_500));
                tab.setBackgroundResource(R.drawable.tab_selected_background);
            } else {
                tab.setTextColor(getResources().getColor(R.color.white));
                tab.setBackgroundResource(R.drawable.tab_unselected_background);
            }
        }
    }
}
