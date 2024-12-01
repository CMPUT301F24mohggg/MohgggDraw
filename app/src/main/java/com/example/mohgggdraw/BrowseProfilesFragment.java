package com.example.mohgggdraw;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.firestore.CollectionReference;


/***
 This fragment manages browsing all the user profiles. It:
 - Sets up a ViewPager2 to navigate between different creation steps
 - Handles navigation between steps (back buttons)
 - Updates UI elements
 ***/
public class BrowseProfilesFragment extends Fragment {
    private ViewPager2 viewPager2;
    private ViewPager2.OnPageChangeCallback pageChangeCallback;
    private BrowseProfilesPagerAdapter browseProfilesPagerAdapter;

    private TextView facilitiesTab;
    private TextView usersTab;
    private TextView imagesTab;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_browse_profiles, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Link Buttons
        facilitiesTab = view.findViewById(R.id.tab_facilities);
        usersTab = view.findViewById(R.id.tab_users);
        imagesTab = view.findViewById(R.id.tab_images);

        // Set up adapter for ViewPager2
        viewPager2 = view.findViewById(R.id.browse_profiles_viewpager);
        BrowseProfilesPagerAdapter adapter = new BrowseProfilesPagerAdapter(this);
        viewPager2.setAdapter(adapter);



        facilitiesTab.setOnClickListener(v -> {
            if (viewPager2.getCurrentItem() != 0) {
                updateTabBarFacilities();
                swapToFragment(0);
            }
        });

        usersTab.setOnClickListener(v -> {
            if (viewPager2.getCurrentItem() != 1) {
                updateTabBarUsers();
                swapToFragment(1);
            }
        });

        imagesTab.setOnClickListener(v -> { // Handle Images tab click
            if (viewPager2.getCurrentItem() != 2) {
                updateTabBarImages();
                swapToFragment(2);
            }
        });

        // Defaults to Facilities fragment
        swapToFragment(0);

    }

    // Defaults tab to facilities
    @Override
    public void onResume() {
        super.onResume();
        swapToFragment(0);
    }


    public void swapToFragment(int position) {
        if (viewPager2 != null) {
            viewPager2.setCurrentItem(position);
        }
    }




    private void updateTabBarFacilities() {
        // Set Facilities tab as selected
        facilitiesTab.setBackgroundResource(R.drawable.tab_selected_background);
        facilitiesTab.setTextColor(getResources().getColor(R.color.tab_selector_text_color, null));

        // Set Users tab as unselected
        usersTab.setBackgroundResource(R.drawable.tab_unselected_background);
        usersTab.setTextColor(getResources().getColor(R.color.tab_unselected_text_color, null));

        imagesTab.setBackgroundResource(R.drawable.tab_unselected_background); // Unselect Images tab
        imagesTab.setTextColor(getResources().getColor(R.color.tab_unselected_text_color, null));
    }



    private void updateTabBarUsers() {
        // Set Users tab as selected
        usersTab.setBackgroundResource(R.drawable.tab_selected_background);
        usersTab.setTextColor(getResources().getColor(R.color.tab_selector_text_color, null));

        // Set Facilities tab as unselected
        facilitiesTab.setBackgroundResource(R.drawable.tab_unselected_background);
        facilitiesTab.setTextColor(getResources().getColor(R.color.tab_unselected_text_color, null));

        imagesTab.setBackgroundResource(R.drawable.tab_unselected_background); // Unselect Images tab
        imagesTab.setTextColor(getResources().getColor(R.color.tab_unselected_text_color, null));
    }

    private void updateTabBarImages() {
        imagesTab.setBackgroundResource(R.drawable.tab_selected_background);
        imagesTab.setTextColor(getResources().getColor(R.color.tab_selector_text_color, null));

        facilitiesTab.setBackgroundResource(R.drawable.tab_unselected_background);
        facilitiesTab.setTextColor(getResources().getColor(R.color.tab_unselected_text_color, null));

        usersTab.setBackgroundResource(R.drawable.tab_unselected_background);
        usersTab.setTextColor(getResources().getColor(R.color.tab_unselected_text_color, null));
    }

}


