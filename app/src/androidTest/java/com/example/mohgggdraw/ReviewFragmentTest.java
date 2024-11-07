package com.example.mohgggdraw;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.os.IBinder;
import android.view.WindowManager;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.espresso.Root;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ReviewFragmentTest {

    @Before
    public void setUp() {
        FragmentScenario.launchInContainer(ReviewFragment.class);
    }

    @Test
    public void testEventCreationButton() {
        // Click on the "Create Event" button in ReviewFragment
        onView(withId(R.id.button_create_event)).perform(click());

        // Retry mechanism for checking the Toast message
        onView(withText("Event successfully created and uploaded to Firestore!"))
                .inRoot(new ToastMatcher())
                .check(matches(withText("Event successfully created and uploaded to Firestore!")));
    }

    // Inner class for matching Toast messages
    public static class ToastMatcher extends TypeSafeMatcher<Root> {
        @Override
        public void describeTo(Description description) {
            description.appendText("is toast");
        }

        @Override
        public boolean matchesSafely(Root root) {
            int type = root.getWindowLayoutParams().get().type;
            if (type == WindowManager.LayoutParams.TYPE_TOAST) {
                IBinder windowToken = root.getDecorView().getWindowToken();
                IBinder appToken = root.getDecorView().getApplicationWindowToken();
                return windowToken == appToken;
            }
            return false;
        }
    }
}
