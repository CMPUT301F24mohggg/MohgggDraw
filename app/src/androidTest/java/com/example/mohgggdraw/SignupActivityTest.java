package com.example.mohgggdraw;

import android.content.Intent;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class SignupActivityTest {

    @Test
    public void testNavigateToFormScreen_Entrant() {
        try (ActivityScenario<SignupActivity> scenario = ActivityScenario.launch(SignupActivity.class)) {
            scenario.onActivity(activity -> {
                activity.navigateToFormScreen("entrant");

                Intent expectedIntent = new Intent(activity, UserFormActivity.class);
                expectedIntent.putExtra("userType", "entrant");

                Intent actualIntent = activity.getIntent();
                assertEquals(expectedIntent.getStringExtra("userType"), actualIntent.getStringExtra("userType"));
            });
        }
    }

    @Test
    public void testNavigateToFormScreen_Facility() {
        try (ActivityScenario<SignupActivity> scenario = ActivityScenario.launch(SignupActivity.class)) {
            scenario.onActivity(activity -> {
                activity.navigateToFormScreen("facility");

                Intent expectedIntent = new Intent(activity, UserFormActivity.class);
                expectedIntent.putExtra("userType", "facility");

                Intent actualIntent = activity.getIntent();
                assertEquals(expectedIntent.getStringExtra("userType"), actualIntent.getStringExtra("userType"));
            });
        }
    }
}
