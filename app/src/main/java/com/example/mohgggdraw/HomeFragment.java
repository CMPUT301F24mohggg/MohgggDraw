package com.example.mohgggdraw;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;
/***
 This fragment represents the home screen of the application. It:
 - Inflates the home screen layout
 - Sets up any necessary UI components or listeners
 ***/
public class HomeFragment extends Fragment implements ListSelectionFragment.ListSelectionListener{
    private OrganizerViewModel organizerViewModel;
    private ViewPager2 viewPager2;
    private ImageView backButton;
    private User user = new User();
    private WaitlistPagerAdapter waitlistAdapter;
    private Boolean orgFlag = false;
    private boolean adminView = false;

    /**
     * Inflates the layout for this fragment.
     *
     * @param inflater LayoutInflater to inflate the views in this fragment.
     * @param container The parent view to attach this fragment to.
     * @param savedInstanceState The previously saved state of the fragment, if any.
     * @return The root view of the fragment.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        OrganizerViewModel createViewModel = new ViewModelProvider(requireActivity()).get(OrganizerViewModel.class);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    /**
     * Called after the fragment's view is created.
     * Sets up the pager adapter, back button, and page navigation logic.
     *
     * @param view The view returned by {@link #onCreateView}.
     * @param savedInstanceState The previously saved state of the fragment, if any.
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //sets pager adapter to transition between homepages and event view pages
        waitlistAdapter = new WaitlistPagerAdapter(this, user);
        if(adminView){
            waitlistAdapter.setAdminView();
        }
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
                if (orgFlag && viewPager2.getCurrentItem() == 3){
                    orgFlag = false;
                    viewPager2.setCurrentItem(viewPager2.getCurrentItem() - 2,false);
                } else {
                    viewPager2.setCurrentItem(viewPager2.getCurrentItem() - 1, false);
                }
            }
        });
    }
    /**
     * Navigates to the event view page.
     *
     * @param event The selected event.
     */
    public void goToNextPage(Event event){
        waitlistAdapter.setEvent(event);
        viewPager2.setAdapter(waitlistAdapter);
        viewPager2.setCurrentItem(1,false);
        backButton.setVisibility(View.VISIBLE);

    }
    /**
     * Navigates to the waitlist view page for a specific event.
     *
     * @param event The selected event.
     */
    public void goToWaitlistView(Event event){
        waitlistAdapter.setEvent(event);
        viewPager2.setAdapter(waitlistAdapter);
        viewPager2.setCurrentItem(2,false);
        backButton.setVisibility(View.VISIBLE);

    }

    /**
     * Navigates to the send notification view.
     *
     * @param event The selected event.
     */
    public void goToSendNotificationView(Event event) {
        orgFlag = true;
        waitlistAdapter.setEvent(event);
        viewPager2.setAdapter(waitlistAdapter);
        viewPager2.setCurrentItem(3,false);
        backButton.setVisibility(View.VISIBLE);
    }

    /**
     * Navigates to the notification details view.
     *
     * @param event The selected event.
     * @param selectedLists The list of selected items related to the event.
     */
    public void goToNotificationDetailsView(Event event, List<String> selectedLists) {
        orgFlag = true;
        NotificationDetailsFragment notificationFragment = new NotificationDetailsFragment(event, selectedLists, this);

        waitlistAdapter.setNotificationDetailsFragment(notificationFragment);
        viewPager2.setAdapter(waitlistAdapter);
        viewPager2.setCurrentItem(4,false);
        backButton.setVisibility(View.VISIBLE);
    }

    /**
     * Handles the selection of lists from the ListSelectionFragment.
     *
     * @param event The selected event.
     * @param selectedLists The list of selected items related to the event.
     */
    @Override
    public void onListsSelected(Event event, List<String> selectedLists) {
        goToNotificationDetailsView(event, selectedLists);
    }
    /**
     * Sets the admin view flag for the fragment.
     */
    public void setAdminView(){
        adminView = true;

    }
    /**
     * Returns the ViewPager2 instance used in the fragment.
     *
     * @return The ViewPager2 instance.
     */
    public ViewPager2 getViewPager2() {
        return viewPager2;
    }


}
