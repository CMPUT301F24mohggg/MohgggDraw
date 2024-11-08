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

/***
 This fragment represents the home screen of the application. It:
 - Inflates the home screen layout
 - Sets up any necessary UI components or listeners
 ***/
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
        //sets pager adapter to transition between homepages and event view pages
        waitlistAdapter = new WaitlistPagerAdapter(this, user);
        organizerViewModel = new ViewModelProvider(requireActivity()).get(OrganizerViewModel.class);
        viewPager2 = view.findViewById(R.id.waitlist_viewpage);
        backButton = view.findViewById(R.id.waitlist_back_button);


        viewPager2.setAdapter(waitlistAdapter);
        //main eventview page
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
        //goest to main home
        backButton.setOnClickListener(v -> {
            if (viewPager2.getCurrentItem() > 0) {
                viewPager2.setCurrentItem(0);
            }
        });
    }
    //goes to eventview
    public void goToNextPage(Event event){
        waitlistAdapter.setEvent(event);
        viewPager2.setAdapter(waitlistAdapter);
        viewPager2.setCurrentItem(1);
        backButton.setVisibility(View.VISIBLE);

    }
    //waitlist view
    public void goToWaitlistView(Event event){
        waitlistAdapter.setEvent(event);
        viewPager2.setAdapter(waitlistAdapter);
        viewPager2.setCurrentItem(2);
        backButton.setVisibility(View.VISIBLE);

    }
}
