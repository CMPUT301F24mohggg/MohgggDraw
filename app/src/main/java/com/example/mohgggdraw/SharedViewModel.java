package com.example.mohgggdraw;

import androidx.lifecycle.*;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.mohgggdraw.databinding.FragmentSecondBinding;

/***
 This SharedViewModel serves as a shared data store for the event creation process. It:
 - Holds LiveData for all event details (title, location, dates, settings, etc.)
 - Provides getters and setters for all stored data
 - Allows fragments to observe and update event creation data
 ***/
public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> eventTitle = new MutableLiveData<>();
    private final MutableLiveData<String> eventLocation = new MutableLiveData<>();
    private final MutableLiveData<String> eventDetail = new MutableLiveData<>();
    private final MutableLiveData<String> registrationOpen = new MutableLiveData<>();
    private final MutableLiveData<String> registrationDeadline = new MutableLiveData<>();
    private final MutableLiveData<String> eventStartTime = new MutableLiveData<>();
    private final MutableLiveData<String> eventEndTime = new MutableLiveData<>();
    private final MutableLiveData<String> maxPoolingSample = new MutableLiveData<>();
    private final MutableLiveData<String> maxEntrants = new MutableLiveData<>();
    private final MutableLiveData<Boolean> enableGeolocation = new MutableLiveData<>();
    private final MutableLiveData<String> imageUrl = new MutableLiveData<>();
    private final MutableLiveData<Boolean> triggerSaveEvent = new MutableLiveData<>();
    private final MutableLiveData<Integer> newPosition = new MutableLiveData<>();
    private final MutableLiveData<EventQr> eventQr = new MutableLiveData<>();
    private final MutableLiveData<Event> event = new MutableLiveData<>();

    public SharedViewModel() {
        this.newPosition.setValue(0);
    }

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

    public LiveData<String> getEventEndTime() { return eventEndTime; }
    public void setEventEndTime(String endTime) { eventEndTime.setValue(endTime); }

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


    public LiveData<Event> getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event.setValue(event);
    }

    public LiveData<EventQr> getEventQr() {
        return eventQr;
    }

    public void setEventQr(EventQr eventQr) {
        this.eventQr.setValue(eventQr);
    }

    public LiveData<Integer> getNewPosition() {
        return newPosition;
    }

    public void setNewPosition(int position) {
        this.newPosition.setValue(position);
    }

    // Method to update geolocation status based on an integer input
    public void setGeolocationValue(int i) {
        enableGeolocation.setValue(i == 1); // Sets true if i is 1, false otherwise
    }
}
