package com.example.mohgggdraw;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NotificationUtils {

    public static void sendNotification(String title, String details, ArrayList<String> deviceIds, String eventId, String status) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Format the timestamp
//        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm:ss a z", Locale.US);
//        String formattedTimestamp = dateFormat.format(new Date());
        
        Timestamp fireStoreTimestamp = Timestamp.now();

        for (String deviceId : deviceIds) {
            // Construct the notification data
            Map<String, Object> notificationData = new HashMap<>();
            notificationData.put("title", title);
            notificationData.put("message", details);
            notificationData.put("eventId", eventId);
            notificationData.put("deviceId", deviceId);
            notificationData.put("status", status);
//            notificationData.put("status", "selected");
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
    }

}
