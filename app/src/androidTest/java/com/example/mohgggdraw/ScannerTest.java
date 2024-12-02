package com.example.mohgggdraw;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ScannerTest {

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    private ScannerViewModel scannerViewModel;

    @Before
    public void setUp() {
        scannerViewModel = new ScannerViewModel();
    }

    @Test
    public void testSetAndGetScanStatus() {
        // Set scan status
        scannerViewModel.setScanStatus(2);

        // Verify the scan status
        assertEquals(2, scannerViewModel.getScanStatus());
    }

    @Test
    public void testSetAndGetNewPosition() {
        // Set new fragment position
        scannerViewModel.setNewPosition(1);

        // Verify the fragment position
        LiveData<Integer> liveData = scannerViewModel.getNewPosition();
        assertEquals(Integer.valueOf(1), liveData.getValue());
    }
}