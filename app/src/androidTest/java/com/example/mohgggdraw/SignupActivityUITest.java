package com.example.mohgggdraw;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


@RunWith(AndroidJUnit4.class)
public class SignupActivityUITest {

    @Rule
    public ActivityScenarioRule<SignupActivity> activityScenarioRule = new ActivityScenarioRule<>(SignupActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @Test
    public void testEntrantButton_NavigatesToUserFormActivity() {
        // Perform click on Entrant button
        onView(withId(R.id.buttonEntrant)).perform(click());

        // Check if the UserFormActivity is launched with the correct extra
        intended(IntentMatchers.hasComponent(UserFormActivity.class.getName()));
        intended(hasExtra("userType", "entrant"));
    }

    @Test
    public void testFacilityButton_NavigatesToUserFormActivity() {
        // Perform click on Facility button
        onView(withId(R.id.buttonFacility)).perform(click());

        // Check if the UserFormActivity is launched with the correct extra
        intended(IntentMatchers.hasComponent(UserFormActivity.class.getName()));
        intended(hasExtra("userType", "facility"));
    }
}
