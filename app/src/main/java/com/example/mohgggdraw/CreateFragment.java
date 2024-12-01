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

public class CreateFragment extends Fragment {

    private ViewPager2 viewPager2;
    private Button nextButton;
    private ImageView backButton;
    private ProgressBar progressBar;
    private TextView pageTitle;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager2 = view.findViewById(R.id.view_pager);
        backButton = view.findViewById(R.id.back_button);
        nextButton = view.findViewById(R.id.next_button);
        progressBar = view.findViewById(R.id.progress_bar);
        pageTitle = view.findViewById(R.id.title_text);

        CreatePagerAdapter adapter = new CreatePagerAdapter(this);
        viewPager2.setAdapter(adapter);

        // Set initial visibility for the back button
        updateBackButtonVisibility(0);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateProgressBar(position, adapter.getItemCount());
                updatePageTitle(position);
                updateBackButtonVisibility(position);

                Fragment currentFragment = getChildFragmentManager().findFragmentByTag("f" + position);
                if (currentFragment instanceof BasicInformationFragment) {
                    setNextButtonEnabled(((BasicInformationFragment) currentFragment).isFormValid());
                } else if (currentFragment instanceof ParticipationSettingsFragment) {
                    setNextButtonEnabled(((ParticipationSettingsFragment) currentFragment).isFormValid());
                } else if (currentFragment instanceof RegistrationDetailsFragment) {
                    setNextButtonEnabled(((RegistrationDetailsFragment) currentFragment).isFormValid());
                }
            }
        });

        nextButton.setOnClickListener(v -> {
            int currentItem = viewPager2.getCurrentItem();
            Fragment currentFragment = getChildFragmentManager().findFragmentByTag("f" + currentItem);
            if (currentFragment instanceof BasicInformationFragment) {
                ((BasicInformationFragment) currentFragment).saveData();
            } else if (currentFragment instanceof ParticipationSettingsFragment) {
                ((ParticipationSettingsFragment) currentFragment).saveData();
            } else if (currentFragment instanceof RegistrationDetailsFragment) {
                ((RegistrationDetailsFragment) currentFragment).saveData();
            }

            if (currentItem < adapter.getItemCount() - 1) {
                viewPager2.setCurrentItem(currentItem + 1);
            }
        });

        backButton.setOnClickListener(v -> {
            int currentItem = viewPager2.getCurrentItem();
            if (currentItem > 0) {
                viewPager2.setCurrentItem(currentItem - 1);
            }
        });
    }

    private void updateBackButtonVisibility(int position) {
        if (position == 0) {
            backButton.setVisibility(View.INVISIBLE); // Hide back button on the first fragment
        } else {
            backButton.setVisibility(View.VISIBLE); // Show back button on other fragments
        }
    }

    private void updateProgressBar(int position, int totalSteps) {
        int progress = (int) ((position + 1) / (float) totalSteps * 100);
        progressBar.setProgress(progress);
    }

    private void updatePageTitle(int position) {
        String[] titles = {"Basic Information", "Registration Details", "Participation Settings", "Review"};
        if (position < titles.length) {
            pageTitle.setText(titles[position]);
        }
    }

    public void setNextButtonEnabled(boolean isEnabled) {
        if (nextButton != null) {
            nextButton.setEnabled(isEnabled);
            nextButton.setAlpha(isEnabled ? 1.0f : 0.5f); // Visual feedback for enabled/disabled state
        }
    }

    public void swapToFragment(int position) {
        if (viewPager2 != null) {
            viewPager2.setCurrentItem(position);
        }
    }
}
