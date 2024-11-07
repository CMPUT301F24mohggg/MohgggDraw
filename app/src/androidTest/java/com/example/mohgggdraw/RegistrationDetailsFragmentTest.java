package com.example.mohgggdraw;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import static org.hamcrest.Matchers.not;


import com.example.mohgggdraw.R;
import com.example.mohgggdraw.RegistrationDetailsFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RegistrationDetailsFragmentTest {

    @Before
    public void setUp() {
        FragmentScenario.launchInContainer(RegistrationDetailsFragment.class);
    }

    @Test
    public void testRegistrationOpenDateSelection() {
        // Simulate clicking on the registration open date field to open date picker
        onView(withId(R.id.registration_open)).perform(click());

        // Select a date from the date picker dialog (assuming the UI action is handled)
        onView(withText("OK")).perform(click());

        // Verify the selected date is displayed in the EditText
        onView(withId(R.id.registration_open)).check(matches(not(withText(""))));
    }

    @Test
    public void testRegistrationDeadlineDateSelection() {
        onView(withId(R.id.registration_deadline)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.registration_deadline)).check(matches(not(withText(""))));
    }

    @Test
    public void testEventStartDateSelection() {
        onView(withId(R.id.event_start_time)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.event_start_time)).check(matches(not(withText(""))));
    }
}
