package com.example.mohgggdraw;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.os.Bundle;

import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LeaveWaitlistTest {

    @Before
    public void setUp() {
        User user = new User();
        Bundle mybun = new Bundle();
        mybun.putBoolean("Geo",false);
        FragmentScenario scenario = FragmentScenario.launchInContainer(WaitlistFragment.class,mybun,new FragmentFactory());

    }

    @Test
    public void testLeaveWaitlist() {
        onView(withId(R.id.eventInfoButton)).perform(click());

        onView(withText("Leave event")).check(matches(isDisplayed()));
        onView(withId(R.id.eventInfoButton)).perform(click());
        onView(withId(R.id.leaveButton)).perform(click());
        onView(withText("Join event")).check(matches(isDisplayed()));



    }
}
