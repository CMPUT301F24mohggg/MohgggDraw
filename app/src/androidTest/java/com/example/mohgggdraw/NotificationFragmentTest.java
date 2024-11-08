package com.example.mohgggdraw;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.espresso.matcher.ViewMatchers;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NotificationFragmentTest {

    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS);

    private FragmentScenario<NotificationFragment> scenario;

    @Before
    public void setup() {
        // Launch NotificationFragment
        scenario = FragmentScenario.launchInContainer(NotificationFragment.class);
    }

    @Test
    public void testRecyclerViewIsDisplayed() {
        // Verify that the RecyclerView is displayed in the fragment
        onView(withId(R.id.recyclerViewNotifications)).check(matches(isDisplayed()));
    }

    @Test
    public void testRecyclerViewIsInitialized() {
        // Verify that the RecyclerView is initialized
        scenario.onFragment(fragment -> {
            RecyclerView recyclerView = fragment.getView().findViewById(R.id.recyclerViewNotifications);
            assertNotNull("RecyclerView should be initialized", recyclerView);
            assertNotNull("RecyclerView should have an adapter", recyclerView.getAdapter());
        });
    }
}
