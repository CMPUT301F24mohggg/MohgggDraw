package com.example.mohgggdraw;

public class NotificationModel {
    private String title;
    private String message;
    private String eventDetail;
    private String status;
    private String eventId;
    private String notificationId;
    private String deviceId;
    private String startTime;

    // Empty constructor required for Firestore serialization
    public NotificationModel() {}

    public NotificationModel(String title, String message, String status, String eventId, String notificationId) {
        this.title = title;
        this.message = message;
        this.status = status;
        this.eventId = eventId;
        this.notificationId = notificationId;
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

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEventDetail() {
        return eventDetail;
    }

    public void setEventDetail(String eventDetail) {
        this.eventDetail = eventDetail;
    }
}
