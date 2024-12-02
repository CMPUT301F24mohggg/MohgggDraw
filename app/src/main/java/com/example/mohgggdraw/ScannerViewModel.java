package com.example.mohgggdraw;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel for managing the state and data for the Scanner feature.
 * Handles navigation, the scanned QR code text, and its validation status.
 */
public class ScannerViewModel extends ViewModel {
    private final MutableLiveData<Integer> newPosition = new MutableLiveData<>();
    private final MutableLiveData<Event> scannedEvent = new MutableLiveData<>();
    private final MutableLiveData<Integer> scanStatus = new MutableLiveData<>();

    /**
     * Initializes the ViewModel with default values.
     */
    public ScannerViewModel() {
        scanStatus.setValue(1);
    }

    /**
     * Sets the scanned event to be shared across components.
     *
     * @param event The scanned event.
     */
    public void setSharedEvent(Event event) {
        scannedEvent.setValue(event);
    }

    /**
     * Retrieves the shared event.
     *
     * @return LiveData containing the scanned event.
     */
    public LiveData<Event> getSharedEvent() {
        return scannedEvent;
    }

    /**
     * Updates the scan status.
     *
     * @param status The new scan status.
     */
    public void setScanStatus(int status) {
        scanStatus.setValue(status);
    }

    /**
     * Retrieves the current scan status.
     *
     * @return The current scan status.
     */
    public int getScanStatus() {
        return scanStatus.getValue();
    }

    /**
     * Sets the new fragment position to navigate to.
     *
     * @param fragmentIndex The index of the fragment to navigate to.
     */
    public void setNewPosition(int fragmentIndex) {
        newPosition.setValue(fragmentIndex);
    }
}