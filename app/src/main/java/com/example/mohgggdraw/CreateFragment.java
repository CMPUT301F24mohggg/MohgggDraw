package com.example.mohgggdraw;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

public class CreateFragment extends Fragment {

    private ViewPager2 viewPager2;
    private Button nextButton;
    private ImageView backButton;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager2 = view.findViewById(R.id.view_pager);
        backButton = view.findViewById(R.id.back_button);
        nextButton = view.findViewById(R.id.next_button);
        progressBar = view.findViewById(R.id.progress_bar);

        // Set up the adapter for ViewPager2
        CreatePagerAdapter adapter = new CreatePagerAdapter(this);
        viewPager2.setAdapter(adapter);

        // Hide the back button on the default page
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                int progress = (position + 1) * 25;
                progressBar.setProgress(progress);

                if (position == 0) {
                    backButton.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    backButton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });

        // Implement back button click listener
        backButton.setOnClickListener(v -> {
            if (viewPager2.getCurrentItem() > 0) {
                viewPager2.setCurrentItem(viewPager2.getCurrentItem() - 1);
            }
        });

        // Implement next button click listener to move to the next page
        nextButton.setOnClickListener(v -> {
            if (viewPager2.getCurrentItem() < adapter.getItemCount() - 1) {
                viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
            }
        });
    }

}
