package com.example.mohgggdraw;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class BasicInformationFragmentTest {

    //@Rule
    //public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        FragmentScenario.launchInContainer(BasicInformationFragment.class);
    }

    @Test
    public void testBasicInformationEntry() {
        // Type text in the EditText fields and verify the entered text
        onView(withId(R.id.event_title)).perform(typeText("Sample Event"), closeSoftKeyboard())
                .check(matches(withText("Sample Event")));
        onView(withId(R.id.event_location)).perform(typeText("Sample Location"), closeSoftKeyboard())
                .check(matches(withText("Sample Location")));
        onView(withId(R.id.event_detail)).perform(typeText("Sample Details"), closeSoftKeyboard())
                .check(matches(withText("Sample Details")));
    }

    @Test
    public void testImageSelectionAndUpload() {
        // Initialize Intents to intercept the image picking intent
        Intents.init();

        // Create a mock image URI for testing
        Uri imageUri = Uri.parse("content://dummy_image_uri");

        // Set up an intent result for the image picker
        Intent resultData = new Intent();
        resultData.setData(imageUri);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        // Stub the intent to pick an image and return the result
        Intents.intending(IntentMatchers.hasAction(Intent.ACTION_PICK)).respondWith(result);

        // Click on the ImageView to trigger image selection
        onView(withId(R.id.organizer_event_poster)).perform(click());

        // Verify that the intent was sent
        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_PICK));

        // Clean up Intents
        Intents.release();
    }
}
