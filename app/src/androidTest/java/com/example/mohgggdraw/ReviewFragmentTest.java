package com.example.mohgggdraw;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ReviewFragmentTest {

    @Before
    public void setUp() {
        // Launch the ReviewFragment for testing
        FragmentScenario.launchInContainer(ReviewFragment.class);
    }

    @Test
    public void testEventCreationButton() {
        // Check if the "Create Event" button can be clicked
        onView(withId(R.id.button_create_event)).perform(click());

    }
}
