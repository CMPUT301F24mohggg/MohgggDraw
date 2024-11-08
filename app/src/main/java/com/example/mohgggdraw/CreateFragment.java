package com.example.mohgggdraw;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
/***
 This fragment manages the creation process for events. It:
 - Sets up a ViewPager2 to navigate between different creation steps
 - Handles navigation between steps (next and back buttons)
 - Updates UI elements like progress bar and page title
 - Saves data for the current page before moving to the next
 ***/
public class CreateFragment extends Fragment {

    private ViewPager2 viewPager2;
    private Button nextButton;
    private ImageView backButton;
    private ProgressBar progressBar;
    private OrganizerViewModel organizerViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        OrganizerViewModel createViewModel = new ViewModelProvider(requireActivity()).get(OrganizerViewModel.class);
        return inflater.inflate(R.layout.fragment_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        organizerViewModel = new ViewModelProvider(requireActivity()).get(OrganizerViewModel.class);
        viewPager2 = view.findViewById(R.id.view_pager);
        backButton = view.findViewById(R.id.back_button);
        nextButton = view.findViewById(R.id.next_button);
        progressBar = view.findViewById(R.id.progress_bar);

        TextView pageTitle = view.findViewById(R.id.title_text);

        // Set up adapter for ViewPager2
        CreatePagerAdapter adapter = new CreatePagerAdapter(this);
        viewPager2.setAdapter(adapter);

        // Hide the back button on the default page
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                int progress = (position + 1) * 25;
                progressBar.setProgress(progress);
                updateUIForPosition(position, pageTitle);
            }
        });

        // Back button click listener
        backButton.setOnClickListener(v -> {
            if (viewPager2.getCurrentItem() > 0) {
                viewPager2.setCurrentItem(viewPager2.getCurrentItem() - 1);
            }
        });

        // Next button click listener
        nextButton.setOnClickListener(v -> {
            saveCurrentPageData(); // Save data for the current page
            if (viewPager2.getCurrentItem() < adapter.getItemCount() - 1) {
                viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
            }
        });
    }

    private void updateUIForPosition(int position, TextView pageTitle) {
        String title;
        boolean isBackButtonVisible = true;
        boolean isNextButtonVisible = true;
        boolean isProgressBarVisible = true;

        switch (position) {
            case 0:
                title = "Basic Information";
                isBackButtonVisible = false;
                break;
            case 1:
                title = "Registration Details";
                break;
            case 2:
                title = "Participation Settings";
                break;
            case 3:
                title = "Review";
                isNextButtonVisible = false;
                break;
            default:
                title = "";
                break;
        }

        pageTitle.setText(title);
        backButton.setVisibility(isBackButtonVisible ? View.VISIBLE : View.INVISIBLE);
        nextButton.setVisibility(isNextButtonVisible ? View.VISIBLE : View.INVISIBLE);
        progressBar.setVisibility(isProgressBarVisible ? View.VISIBLE : View.INVISIBLE);
    }

    private void saveCurrentPageData() {
        // Get the current fragment in the ViewPager2
        Fragment currentFragment = getChildFragmentManager().findFragmentByTag("f" + viewPager2.getCurrentItem());

        // Check the type of the current fragment and call their respective save methods
        if (currentFragment instanceof BasicInformationFragment) {
            ((BasicInformationFragment) currentFragment).saveData();
        } else if (currentFragment instanceof RegistrationDetailsFragment) {
            ((RegistrationDetailsFragment) currentFragment).saveData();
        } else if (currentFragment instanceof ParticipationSettingsFragment) {
            ((ParticipationSettingsFragment) currentFragment).saveData();
        }
        // Add other fragments as needed
    }
}
