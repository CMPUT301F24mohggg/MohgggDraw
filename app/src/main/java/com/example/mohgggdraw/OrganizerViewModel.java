package com.example.mohgggdraw;

import android.net.Uri;

import androidx.lifecycle.ViewModel;

public class OrganizerViewModel extends ViewModel {
    private Uri eventImageUri;
    private String eventTitle;
    private String eventLocation;
    private String eventDetail;

    // Getters and setters for image URI
    public Uri getEventImageUri() {
        return eventImageUri;
    }

    public void setEventImageUri(Uri eventImageUri) {
        this.eventImageUri = eventImageUri;
    }

    // Getters and setters for event details
    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getEventDetail() {
        return eventDetail;
    }

    public void setEventDetail(String eventDetail) {
        this.eventDetail = eventDetail;
    }
}
