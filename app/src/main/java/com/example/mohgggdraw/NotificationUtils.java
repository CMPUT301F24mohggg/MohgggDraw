package com.example.mohgggdraw;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Utility class for handling the creation and sending of notifications to devices.
 * This class interacts with Firestore to filter devices that have opted out of notifications
 * and sends notifications to eligible devices.
 */
public class NotificationUtils {
    /**
     * Sends a notification to a list of devices, excluding those that have opted out.
     * Notifications are saved in the Firestore "notification" collection.
     *
     * @param title     The title of the notification.
     * @param details   The content of the notification.
     * @param deviceIds A list of device IDs to send the notification to.
     * @param eventId   The event ID associated with the notification.
     * @param status    The status of the notification (e.g., "sent", "read").
     */
    public static void sendNotification(String title, String details, ArrayList<String> deviceIds, String eventId, String status) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Retrieve the notificationOptOut document
        db.collection("notification")
                .document("notificationOptOut")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Create a list to store devices that can receive notifications
                        ArrayList<String> filteredDeviceIds = new ArrayList<>(deviceIds);

                        // Check if the document exists and has a deviceIds field
                        if (task.getResult() != null && task.getResult().exists() && task.getResult().contains("deviceIds")) {
                            // Get the list of opted-out device IDs
                            List<String> optedOutDevices = (List<String>) task.getResult().get("deviceIds");

                            if (optedOutDevices != null) {
                                // Log out all opted-out device IDs
                                for (String optedOutDeviceId : optedOutDevices) {
                                    Log.d("NotificationUtils", "Opted-out Device ID: " + optedOutDeviceId);
                                }

                                // Remove opted-out devices from the filtered list
                                filteredDeviceIds.removeAll(optedOutDevices);
                            }
                        }

                        Timestamp fireStoreTimestamp = Timestamp.now();

                        // Send notifications only to non-opted-out devices
                        for (String deviceId : filteredDeviceIds) {
                            // Construct the notification data
                            Map<String, Object> notificationData = new HashMap<>();
                            notificationData.put("title", title);
                            notificationData.put("message", details);
                            notificationData.put("eventId", eventId);
                            notificationData.put("deviceId", deviceId);
                            notificationData.put("status", status);
                            notificationData.put("created_at", fireStoreTimestamp);

                            // Add the notification document with a random ID
                            db.collection("notification")
                                    .add(notificationData)
                                    .addOnSuccessListener(documentReference -> {
                                        Log.d("NotificationUtils", "Notification created with ID: " + documentReference.getId());
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("NotificationUtils", "Failed to create notification", e);
                                    });
                        }
                    } else {
                        // Handle failure in retrieving the document
                        Log.e("NotificationUtils", "Failed to retrieve notificationOptOut document", task.getException());

                        // If document retrieval fails, send notifications to all devices
                        sendNotificationsToAllDevices(title, details, deviceIds, eventId, status);
                    }
                });
    }

    /**
     * Sends notifications to all devices in case the opt-out document retrieval fails.
     * This is a fallback mechanism.
     *
     * @param title     The title of the notification.
     * @param details   The content of the notification.
     * @param deviceIds A list of device IDs to send the notification to.
     * @param eventId   The event ID associated with the notification.
     * @param status    The status of the notification (e.g., "sent", "read").
     */
    private static void sendNotificationsToAllDevices(String title, String details, ArrayList<String> deviceIds, String eventId, String status) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Timestamp fireStoreTimestamp = Timestamp.now();

        for (String deviceId : deviceIds) {
            Map<String, Object> notificationData = new HashMap<>();
            notificationData.put("title", title);
            notificationData.put("message", details);
            notificationData.put("eventId", eventId);
            notificationData.put("deviceId", deviceId);
            notificationData.put("status", status);
            notificationData.put("created_at", fireStoreTimestamp);

            db.collection("notification")
                    .add(notificationData)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("NotificationUtils", "Notification created with ID: " + documentReference.getId());
                    })
                    .addOnFailureListener(e -> {
                        Log.e("NotificationUtils", "Failed to create notification", e);
                    });
        }
    }
}