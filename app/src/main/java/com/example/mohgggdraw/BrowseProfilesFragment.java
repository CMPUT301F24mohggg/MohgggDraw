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

    /**
     * Inflates the fragment layout and sets up the UI components.
     *
     * @param inflater           The LayoutInflater object used to inflate views.
     * @param container          The parent view that this fragment's UI will be attached to.
     * @param savedInstanceState If non-null, contains the previous saved state of the fragment.
     * @return The created view for this fragment.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_browse_profiles, container, false);
    }

    /**
     * Called after the view is created. Sets up the ViewPager2, tab navigation, and click listeners.
     *
     * @param view               The root view of the fragment.
     * @param savedInstanceState If non-null, contains the previous saved state of the fragment.
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Link tab buttons to the layout views
        facilitiesTab = view.findViewById(R.id.tab_facilities);
        usersTab = view.findViewById(R.id.tab_users);
        imagesTab = view.findViewById(R.id.tab_images);

        // Set up the adapter for ViewPager2
        viewPager2 = view.findViewById(R.id.browse_profiles_viewpager);
        BrowseProfilesPagerAdapter adapter = new BrowseProfilesPagerAdapter(this);
        viewPager2.setAdapter(adapter);

        // Set up click listeners for each tab
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

        imagesTab.setOnClickListener(v -> {
            if (viewPager2.getCurrentItem() != 2) {
                updateTabBarImages();
                swapToFragment(2);
            }
        });

        // Default to Facilities tab
        swapToFragment(0);
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
