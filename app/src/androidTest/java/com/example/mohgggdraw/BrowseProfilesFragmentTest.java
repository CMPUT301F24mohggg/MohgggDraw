package com.example.mohgggdraw.test;

import com.example.mohgggdraw.BrowseProfilesFragment;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BrowseProfilesFragmentTest {

    private BrowseProfilesFragment fragment;

    @Before
    public void setUp() {
        fragment = new BrowseProfilesFragment();
    }

    @Test
    public void testGetTabName() {
        // Verify the correct tab names for positions
        assertEquals("Facilities", fragment.getTabName(0));
        assertEquals("Users", fragment.getTabName(1));
        assertEquals("Images", fragment.getTabName(2));
        assertEquals("Unknown", fragment.getTabName(3)); // Invalid position
    }
}