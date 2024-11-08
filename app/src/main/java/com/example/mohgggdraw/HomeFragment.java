package com.example.mohgggdraw;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

public class HomeFragment extends Fragment {
    private OrganizerViewModel organizerViewModel;
    private ViewPager2 viewPager2;
    private ImageView backButton;
    private User user = new User();
    private WaitlistPagerAdapter waitlistAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        OrganizerViewModel createViewModel = new ViewModelProvider(requireActivity()).get(OrganizerViewModel.class);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        waitlistAdapter = new WaitlistPagerAdapter(this, user);
        organizerViewModel = new ViewModelProvider(requireActivity()).get(OrganizerViewModel.class);
        viewPager2 = view.findViewById(R.id.waitlist_viewpage);
        backButton = view.findViewById(R.id.waitlist_back_button);


        viewPager2.setAdapter(waitlistAdapter);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                backButton.setVisibility(View.VISIBLE);
                if(position==0){
                    backButton.setVisibility(View.INVISIBLE);
                }
            }
        });

        backButton.setOnClickListener(v -> {
            if (viewPager2.getCurrentItem() > 0) {
                viewPager2.setCurrentItem(0);
            }
        });
    }
    public void goToNextPage(Event event){
        waitlistAdapter.setEvent(event);
        viewPager2.setAdapter(waitlistAdapter);
        viewPager2.setCurrentItem(1);
        backButton.setVisibility(View.VISIBLE);

    }
    public void goToWaitlistView(Event event){
        waitlistAdapter.setEvent(event);
        viewPager2.setAdapter(waitlistAdapter);
        viewPager2.setCurrentItem(2);
        backButton.setVisibility(View.VISIBLE);

    }
}
