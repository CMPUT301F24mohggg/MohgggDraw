package com.example.mohgggdraw;

import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    private Timestamp startTime;
    private String eventTitle;
    private String startMonth;
    private String startDate;
    private boolean isAccepted;
    private boolean isDeclined;
    private Timestamp created_at;


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



    public Timestamp getStartTime() {
        return startTime;
    }

    /**
     * Sets the start time of the event associated with the notification.
     *
     * @param startTime The start time to set.
     */
    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
        splitStartTime(startTime); // Update startMonth and startDate when startTime changes
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

    /**
     * Splits the startTime into startMonth and startDate.
     *
     * @param startTime The start time in the format "dd/MM/yyyy".
     */
    private void splitStartTime(Timestamp startTime) {
        if (startTime == null) return;

        // Convert Timestamp to Date
        Date date = startTime.toDate();

        // Extract short month and day
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.ENGLISH);
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.ENGLISH);

        this.startMonth = monthFormat.format(date); // Example: "Nov"
        this.startDate = dayFormat.format(date); // Example: "21"
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

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(String startMonth) {
        this.startMonth = startMonth;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }

    public boolean isDeclined() {
        return isDeclined;
    }

    public void setDeclined(boolean declined) {
        isDeclined = declined;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }
}
