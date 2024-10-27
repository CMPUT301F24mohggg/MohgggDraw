// SharedViewModel.java
package com.example.mohgggdraw;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {

    private final MutableLiveData<String> eventTitle = new MutableLiveData<>();
    private final MutableLiveData<String> eventLocation = new MutableLiveData<>();
    private final MutableLiveData<String> eventDetail = new MutableLiveData<>();
    private final MutableLiveData<String> registrationOpen = new MutableLiveData<>();
    private final MutableLiveData<String> registrationDeadline = new MutableLiveData<>();
    private final MutableLiveData<String> eventStartTime = new MutableLiveData<>();
    private final MutableLiveData<String> maxPoolingSample = new MutableLiveData<>();
    private final MutableLiveData<String> maxEntrants = new MutableLiveData<>();
    private final MutableLiveData<Boolean> enableGeolocation = new MutableLiveData<>();
    private final MutableLiveData<String> imageUrl = new MutableLiveData<>();

    public void setEventTitle(String title) {
        eventTitle.setValue(title);
    }

    public LiveData<String> getEventTitle() {
        return eventTitle;
    }

    public void setEventLocation(String location) {
        eventLocation.setValue(location);
    }

    public LiveData<String> getEventLocation() {
        return eventLocation;
    }

    public void setEventDetail(String detail) {
        eventDetail.setValue(detail);
    }

    public LiveData<String> getEventDetail() {
        return eventDetail;
    }

    public void setRegistrationOpen(String date) {
        registrationOpen.setValue(date);
    }

    public LiveData<String> getRegistrationOpen() {
        return registrationOpen;
    }

    public void setRegistrationDeadline(String date) {
        registrationDeadline.setValue(date);
    }

    public LiveData<String> getRegistrationDeadline() {
        return registrationDeadline;
    }

    public void setEventStartTime(String startTime) {
        eventStartTime.setValue(startTime);
    }

    public LiveData<String> getEventStartTime() {
        return eventStartTime;
    }

    public void setMaxPoolingSample(String sample) {
        maxPoolingSample.setValue(sample);
    }

    public LiveData<String> getMaxPoolingSample() {
        return maxPoolingSample;
    }

    public void setMaxEntrants(String entrants) {
        maxEntrants.setValue(entrants);
    }

    public LiveData<String> getMaxEntrants() {
        return maxEntrants;
    }

    public void setEnableGeolocation(boolean enabled) {
        enableGeolocation.setValue(enabled);
    }

    public LiveData<Boolean> getEnableGeolocation() {
        return enableGeolocation;
    }

    ;

    public void setImageUrl(String url) {
        imageUrl.setValue(url);
    }

    public LiveData<String> getImageUrl() {
        return imageUrl;
    }

}

