package com.example.mohgggdraw;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.os.Bundle;

import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.Lifecycle;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)

public class GeolocationUnitTest {


    User user = new User();





    @Before
    public void setUp() {
        User user = new User();
        Bundle mybun = new Bundle();
        mybun.putBoolean("Geo",true);
        FragmentScenario scenario = FragmentScenario.launchInContainer(WaitlistFragment.class,mybun,new FragmentFactory());

    }

    @Test
    public void testJoinGeolocationWaitlist() {
        onView(withId(R.id.eventInfoButton)).perform(click());

        onView(withText("Warning")).check(matches(isDisplayed()));

        onView(withId(R.id.accept_terms_button)).perform(click());
        onView(withText("Leave event")).check(matches(isDisplayed()));


    }
}
