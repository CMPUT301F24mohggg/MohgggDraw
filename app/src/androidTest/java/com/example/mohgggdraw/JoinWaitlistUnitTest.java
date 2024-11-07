package com.example.mohgggdraw;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertTrue;

import android.media.audiofx.Visualizer;
import android.os.Bundle;

import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import androidx.test.filters.LargeTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)

public class JoinWaitlistUnitTest {
    //testing the join to watchlist
    
    User user = new User();






    @Test

    public void testAddToWishlist() throws InterruptedException {

        FragmentScenario<WaitlistFragment> scenario;
        Bundle mybun = new Bundle();
        mybun.putBoolean("Geo",false);

        scenario = FragmentScenario.launchInContainer(WaitlistFragment.class,mybun,new FragmentFactory());


        user.setEmail("mewoowww normal");
        scenario.moveToState(Lifecycle.State.RESUMED);


        //click the join waitlist button
        onView(withId(R.id.eventInfoButton)).perform(click());
        //Thread.sleep(2000);


        //assertTrue(event.getWaitingList().contains("mewoowww normal"));
        onView(withText("Leave event")).check(matches(isDisplayed()));

        //




    }
}

