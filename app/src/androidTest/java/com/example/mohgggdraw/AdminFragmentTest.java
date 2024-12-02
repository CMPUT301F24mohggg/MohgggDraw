package com.example.mohgggdraw;

import static android.app.PendingIntent.getActivity;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.is;
import static java.util.function.Predicate.not;
import static java.util.regex.Pattern.matches;

import android.view.View;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.ViewAssertion;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AdminFragmentTest {

    @Before
    public void setUp() {
        FragmentScenario.launchInContainer(BrowseProfilesFragment.class);
    }


    @Test
    public void testFacilities() {
        onView(withText("Facilities"))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testUsers() {
        onView(withText("Users"))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testImages() {
        onView(withText("Images"))
                .check(matches(isDisplayed()));
    }
}

