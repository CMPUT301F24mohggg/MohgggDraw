package com.example.mohgggdraw;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class UserFormActivityTest {

    @Test
    public void testGetUserTypeCode_Entrant() {
        UserFormActivity activity = new UserFormActivity();
        int result = activity.getUserTypeCode("entrant");
        assertEquals(0, result);
    }

    @Test
    public void testGetUserTypeCode_Organizer() {
        UserFormActivity activity = new UserFormActivity();
        int result = activity.getUserTypeCode("organizer");
        assertEquals(1, result);
    }

    @Test
    public void testGetUserTypeCode_Admin() {
        UserFormActivity activity = new UserFormActivity();
        int result = activity.getUserTypeCode("admin");
        assertEquals(2, result);
    }
}

