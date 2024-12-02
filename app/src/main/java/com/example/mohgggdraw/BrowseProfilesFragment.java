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

/**
 * BrowseProfilesFragment is responsible for managing the UI to browse different user profiles (facilities, users, and images).
 * <p>
 * It uses a ViewPager2 to display different fragments for browsing facilities, users, and images,
 * and provides a tab navigation system to switch between these fragments.
 */
public class BrowseProfilesFragment extends Fragment {

    private ViewPager2 viewPager2;
    private ViewPager2.OnPageChangeCallback pageChangeCallback;
    private BrowseProfilesPagerAdapter browseProfilesPagerAdapter;

    private TextView facilitiesTab;
    private TextView usersTab;
    private TextView imagesTab;
    private TextView eventsTab;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the correct layout for the fragment
        return inflater.inflate(R.layout.fragment_browse_profiles, container, false);
    }

    /**
     * Inflates the fragment layout and sets up the UI components.
     *
     * @param view          The parent view that this fragment's UI will be attached to.
     * @param savedInstanceState If non-null, contains the previous saved state of the fragment.
     * @return The created view for this fragment.
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Hide the trash icon
        ImageView fabDelete = view.findViewById(R.id.fab_delete);
        if (fabDelete != null) {
            fabDelete.setVisibility(View.GONE);
        }

        facilitiesTab = view.findViewById(R.id.tab_facilities);
        usersTab = view.findViewById(R.id.tab_users);
        imagesTab = view.findViewById(R.id.tab_images);
        eventsTab = view.findViewById(R.id.tab_events);

        viewPager2 = view.findViewById(R.id.browse_profiles_viewpager);
        BrowseProfilesPagerAdapter adapter = new BrowseProfilesPagerAdapter(this);
        viewPager2.setAdapter(adapter);

        // Add a page change callback to sync tab highlights with ViewPager2
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        updateTabBarFacilities();
                        break;
                    case 1:
                        updateTabBarUsers();
                        break;
                    case 2:
                        updateTabBarImages();
                        break;
                    case 3:
                        updateTabBarEvents();
                        break;
                }
            }
        });

        // Set tab click listeners
        facilitiesTab.setOnClickListener(v -> {
            updateTabBarFacilities();
            viewPager2.setCurrentItem(0);
        });

        usersTab.setOnClickListener(v -> {
            updateTabBarUsers();
            viewPager2.setCurrentItem(1);
        });

        imagesTab.setOnClickListener(v -> {
            updateTabBarImages();
            viewPager2.setCurrentItem(2);
        });

        eventsTab.setOnClickListener(v -> {
            updateTabBarEvents();
            viewPager2.setCurrentItem(3);
        });

        // Default to Facilities tab
        viewPager2.setCurrentItem(0);
    }


    /**
     * Ensures that the Facilities tab is selected by default when the fragment is resumed.
     */
    @Override
    public void onResume() {
        super.onResume();
        swapToFragment(0);
    }

    /**
     * Switches the ViewPager2 to the specified position.
     *
     * @param position The position of the fragment to display (0 for Facilities, 1 for Users, 2 for Images).
     */
    public void swapToFragment(int position) {
        if (viewPager2 != null) {
            viewPager2.setCurrentItem(position);
        }
    }

    /**
     * Updates the appearance of the tab bar to highlight the Facilities tab as selected.
     */
    private void updateTabBarFacilities() {
        facilitiesTab.setBackgroundResource(R.drawable.tab_selected_background);
        facilitiesTab.setTextColor(getResources().getColor(R.color.tab_selector_text_color, null));

        usersTab.setBackgroundResource(R.drawable.tab_unselected_background);
        usersTab.setTextColor(getResources().getColor(R.color.tab_unselected_text_color, null));

        imagesTab.setBackgroundResource(R.drawable.tab_unselected_background);
        imagesTab.setTextColor(getResources().getColor(R.color.tab_unselected_text_color, null));

        eventsTab.setBackgroundResource(R.drawable.tab_unselected_background);
        eventsTab.setTextColor(getResources().getColor(R.color.tab_unselected_text_color, null));
    }

    /**
     * Updates the appearance of the tab bar to highlight the Users tab as selected.
     */
    private void updateTabBarUsers() {
        usersTab.setBackgroundResource(R.drawable.tab_selected_background);
        usersTab.setTextColor(getResources().getColor(R.color.tab_selector_text_color, null));

        facilitiesTab.setBackgroundResource(R.drawable.tab_unselected_background);
        facilitiesTab.setTextColor(getResources().getColor(R.color.tab_unselected_text_color, null));

        imagesTab.setBackgroundResource(R.drawable.tab_unselected_background);
        imagesTab.setTextColor(getResources().getColor(R.color.tab_unselected_text_color, null));

        eventsTab.setBackgroundResource(R.drawable.tab_unselected_background);
        eventsTab.setTextColor(getResources().getColor(R.color.tab_unselected_text_color, null));
    }

    /**
     * Updates the appearance of the tab bar to highlight the Images tab as selected.
     */
    private void updateTabBarImages() {
        imagesTab.setBackgroundResource(R.drawable.tab_selected_background);
        imagesTab.setTextColor(getResources().getColor(R.color.tab_selector_text_color, null));

        facilitiesTab.setBackgroundResource(R.drawable.tab_unselected_background);
        facilitiesTab.setTextColor(getResources().getColor(R.color.tab_unselected_text_color, null));

        usersTab.setBackgroundResource(R.drawable.tab_unselected_background);
        usersTab.setTextColor(getResources().getColor(R.color.tab_unselected_text_color, null));

        eventsTab.setBackgroundResource(R.drawable.tab_unselected_background);
        eventsTab.setTextColor(getResources().getColor(R.color.tab_unselected_text_color, null));
    }

    /**
     * Updates the appearance of the tab bar to highlight the Events tab as selected.
     */
    private void updateTabBarEvents() {
        eventsTab.setBackgroundResource(R.drawable.tab_selected_background);
        eventsTab.setTextColor(getResources().getColor(R.color.tab_selector_text_color, null));

        facilitiesTab.setBackgroundResource(R.drawable.tab_unselected_background);
        facilitiesTab.setTextColor(getResources().getColor(R.color.tab_unselected_text_color, null));

        usersTab.setBackgroundResource(R.drawable.tab_unselected_background);
        usersTab.setTextColor(getResources().getColor(R.color.tab_unselected_text_color, null));

        imagesTab.setBackgroundResource(R.drawable.tab_unselected_background);
        imagesTab.setTextColor(getResources().getColor(R.color.tab_unselected_text_color, null));
    }


    /**
     * Determines the tab name based on its position.
     *
     * @param position Tab position (0 = Facilities, 1 = Users, 2 = Images).
     * @return The name of the tab.
     */
    public String getTabName(int position) {
        switch (position) {
            case 0:
                return "Facilities";
            case 1:
                return "Users";
            case 2:
                return "Images";
            default:
                return "Unknown";
        }
    }
}
