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
public class ViewWaitlistTest {
    @Before
    public void setUp() {
        User user = new User();
        Bundle mybun = new Bundle();
        mybun.putBoolean("Org",true);
        FragmentScenario scenario = FragmentScenario.launchInContainer(WaitlistFragment.class,mybun,new FragmentFactory());

    }

    @Test
    public void testViewWaitlist(){

        //args specify this is organizer view
        //for some reason testing across multiple fragments crashes
        onView(withText("View waitlist")).check(matches(isDisplayed()));




    }
}
