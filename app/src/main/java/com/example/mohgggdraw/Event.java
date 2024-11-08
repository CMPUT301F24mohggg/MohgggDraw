package com.example.mohgggdraw;

public class Event {
    private String eventId;
    private String title;
    private String location;
    private String posterUrl;
    private String registrationDetails;
    private String participationSettings;

    // Required empty constructor for Firebase
    public Event(String eventId, String value, String location, String posterUrl, String registrationDetails, String participationSettings) {
    }

    public Event(String eventId, String title, String location, String posterUrl, String registrationDetails, String participationSettings, String value, String s, String string, Boolean aBoolean) {
        this.eventId = eventId;
        this.title = title;
        this.location = location;
        this.posterUrl = posterUrl;
        this.registrationDetails = registrationDetails;
        this.participationSettings = participationSettings;
    }

    // Getters and Setters for Firebase to use
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public String getRegistrationDetails() { return registrationDetails; }
    public void setRegistrationDetails(String registrationDetails) { this.registrationDetails = registrationDetails; }

    public String getParticipationSettings() { return participationSettings; }
    public void setParticipationSettings(String participationSettings) { this.participationSettings = participationSettings; }
}
