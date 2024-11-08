package com.example.mohgggdraw;

import android.content.Intent;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.intent.Intents;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class UserFormActivityUITest {

    @Rule
    public ActivityScenarioRule<UserFormActivity> activityRule = new ActivityScenarioRule<>(
            new Intent().putExtra("userType", "entrant"));

    @Before
    public void setUp() {
        Intents.init();
    }

    @Test
    public void testSubmitButtonWithValidData() {
        onView(withId(R.id.editTextName)).perform(replaceText("John Doe"));
        onView(withId(R.id.editTextPhone)).perform(replaceText("1234567890"));
        onView(withId(R.id.editTextEmail)).perform(replaceText("johndoe@example.com"));
        onView(withId(R.id.editTextLocation)).perform(replaceText("New York"));

        onView(withId(R.id.buttonSubmit)).perform(click());

        intended(hasComponent(MainActivity.class.getName()));
    }

    @Test
    public void testSubmitButtonWithMissingData() {
        onView(withId(R.id.editTextName)).perform(replaceText("John Doe"));
        onView(withId(R.id.editTextPhone)).perform(replaceText("1234567890"));
        // Leave email and location empty

        onView(withId(R.id.buttonSubmit)).perform(click());

        onView(withText("Please fill out all fields")).check(matches(withText("Please fill out all fields")));
    }
}

