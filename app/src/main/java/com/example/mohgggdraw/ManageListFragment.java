package com.example.mohgggdraw;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

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

        // Initialize TabLayout and ViewPager2
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);

        // Create a list of fragments for the slider
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new WaitlistFragment());
        //fragments.add(new CancelledListFragment());
        //fragments.add(new SelectedListFragment());
        //fragments.add(new EntrantListFragment());

        // Set up titles for tabs
        List<String> fragmentTitles = new ArrayList<>();
        fragmentTitles.add("Waiting List");
        fragmentTitles.add("Cancelled List");
        fragmentTitles.add("Selected List");
        fragmentTitles.add("Entrant List");

        // Set up adapter
        TabFragmentAdapter adapter = new TabFragmentAdapter(this, fragments);
        viewPager.setAdapter(adapter);

        // Link TabLayout and ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(fragmentTitles.get(position))).attach();
    }
}
