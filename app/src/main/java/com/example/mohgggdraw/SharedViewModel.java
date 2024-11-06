package com.example.mohgggdraw;

import androidx.lifecycle.*;

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
    private final MutableLiveData<Boolean> triggerSaveEvent = new MutableLiveData<>();

    // Getters and setters for all fields
    public LiveData<String> getEventTitle() { return eventTitle; }
    public void setEventTitle(String title) { eventTitle.setValue(title); }

    public LiveData<String> getEventLocation() { return eventLocation; }
    public void setEventLocation(String location) { eventLocation.setValue(location); }

    public LiveData<String> getEventDetail() { return eventDetail; }
    public void setEventDetail(String detail) { eventDetail.setValue(detail); }

    public LiveData<String> getRegistrationOpen() { return registrationOpen; }
    public void setRegistrationOpen(String openDate) { registrationOpen.setValue(openDate); }

    public LiveData<String> getRegistrationDeadline() { return registrationDeadline; }
    public void setRegistrationDeadline(String deadline) { registrationDeadline.setValue(deadline); }

    public LiveData<String> getEventStartTime() { return eventStartTime; }
    public void setEventStartTime(String startTime) { eventStartTime.setValue(startTime); }

    public LiveData<String> getMaxPoolingSample() { return maxPoolingSample; }
    public void setMaxPoolingSample(String sample) { maxPoolingSample.setValue(sample); }

    public LiveData<String> getMaxEntrants() { return maxEntrants; }
    public void setMaxEntrants(String entrants) { maxEntrants.setValue(entrants); }

    public LiveData<Boolean> getEnableGeolocation() { return enableGeolocation; }
    public void setEnableGeolocation(Boolean geolocation) { enableGeolocation.setValue(geolocation); }

    public LiveData<String> getImageUrl() { return imageUrl; }
    public void setImageUrl(String url) { imageUrl.setValue(url); }

    public LiveData<Boolean> getTriggerSaveEvent() { return triggerSaveEvent; }
    public void setTriggerSaveEvent(Boolean trigger) { triggerSaveEvent.setValue(trigger); }

    // Method to update geolocation status based on an integer input
    public void setGeolocationValue(int i) {
        enableGeolocation.setValue(i == 1); // Sets true if i is 1, false otherwise
    }
}
