package com.example.mohgggdraw;

public class NotificationModel {
    private String title;
    private String message;
    private String status;
    private String eventId;

    // Empty constructor required for Firestore serialization
    public NotificationModel() {}

    public NotificationModel(String title, String message, String status, String eventId) {
        this.title = title;
        this.message = message;
        this.status = status;
        this.eventId = eventId;
    }

    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getStatus() { return status; }
    public String getEventId() { return eventId; }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
