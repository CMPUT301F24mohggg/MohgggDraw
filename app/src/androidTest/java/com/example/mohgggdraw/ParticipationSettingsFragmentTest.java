package com.example.mohgggdraw;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ParticipationSettingsFragmentTest {

    @Before
    public void setUp() {
        FragmentScenario.launchInContainer(ParticipationSettingsFragment.class);
    }

    @Test
    public void testMaxPoolingSampleAndEntrantsEntry() {
        onView(withId(R.id.maximum_pooling_sample)).perform(typeText("100"), closeSoftKeyboard())
                .check(matches(withText("100")));
        onView(withId(R.id.max_entrants_optional)).perform(typeText("50"), closeSoftKeyboard())
                .check(matches(withText("50")));
    }

    @Test
    public void testGeolocationToggle() {
        onView(withId(R.id.switch_enable_geolocation)).perform(click())
                .check(matches(isChecked()));
    }
}
