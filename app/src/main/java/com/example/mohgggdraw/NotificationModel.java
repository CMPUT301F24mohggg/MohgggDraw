package com.example.mohgggdraw;

/**
 * Represents a notification with details related to an event.
 * This model is used for displaying notification data and for storing/retrieving
 * notification details from Firestore.
 */
public class NotificationModel {
    private String title;
    private String message;
    private String eventDetail;
    private String status;
    private String eventId;
    private String notificationId;
    private String deviceId;
    private String startTime;

    /**
     * Empty constructor required for Firestore serialization.
     */
    public NotificationModel() {}

    /**
     * Constructs a NotificationModel with the specified parameters.
     *
     * @param title          The title of the notification.
     * @param message        The message content of the notification.
     * @param status         The status of the notification (e.g., selected, declined).
     * @param eventId        The ID of the related event.
     * @param notificationId The unique ID of the notification.
     */
    public NotificationModel(String title, String message, String status, String eventId, String notificationId) {
        this.title = title;
        this.message = message;
        this.status = status;
        this.eventId = eventId;
        this.notificationId = notificationId;
    }

    /**
     * Returns the title of the notification.
     *
     * @return The notification title.
     */
    public String getTitle() { return title; }

    /**
     * Returns the message content of the notification.
     *
     * @return The notification message.
     */
    public String getMessage() { return message; }

    /**
     * Returns the status of the notification.
     *
     * @return The notification status.
     */
    public String getStatus() { return status; }

    /**
     * Returns the ID of the related event.
     *
     * @return The event ID.
     */
    public String getEventId() { return eventId; }

    /**
     * Sets the title of the notification.
     *
     * @param title The title to set for the notification.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the message content of the notification.
     *
     * @param message The message content to set.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Sets the status of the notification.
     *
     * @param status The notification status to set.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Sets the ID of the related event.
     *
     * @param eventId The event ID to set.
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Returns the unique ID of the notification.
     *
     * @return The notification ID.
     */
    public String getNotificationId() {
        return notificationId;
    }

    /**
     * Sets the unique ID of the notification.
     *
     * @param notificationId The notification ID to set.
     */
    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    /**
     * Returns the device ID associated with the notification.
     *
     * @return The device ID.
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Sets the device ID associated with the notification.
     *
     * @param deviceId The device ID to set.
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Returns the start time of the event associated with the notification.
     *
     * @return The event start time.
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * Sets the start time of the event associated with the notification.
     *
     * @param startTime The start time to set.
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * Returns additional details of the event associated with the notification.
     *
     * @return The event details.
     */
    public String getEventDetail() {
        return eventDetail;
    }

    /**
     * Sets additional details of the event associated with the notification.
     *
     * @param eventDetail The event details to set.
     */
    public void setEventDetail(String eventDetail) {
        this.eventDetail = eventDetail;
    }
}
