package com.example.mohgggdraw;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ScannerViewModel extends ViewModel {
    private final MutableLiveData<Integer> newPosition = new MutableLiveData<>();
    private final MutableLiveData<Event> scannedEvent = new MutableLiveData<>();
    private final MutableLiveData<Integer> scanStatus = new MutableLiveData<>();


    public ScannerViewModel() {
        scanStatus.setValue(1);
    }

    // Event Object
    public void setSharedEvent(Event event) {
        scannedEvent.setValue(event);
    }

    public LiveData<Event> getSharedEvent() {
        return scannedEvent;
    }

    // For when the scanner needs to be started
    public void setScanStatus(int status) {
        scanStatus.setValue(status);
    }

    public int getScanStatus() {
        return scanStatus.getValue();
    }

    // Navigation methods
    public void setNewPosition(int fragmentIndex) {
        newPosition.setValue(fragmentIndex);
    }

}