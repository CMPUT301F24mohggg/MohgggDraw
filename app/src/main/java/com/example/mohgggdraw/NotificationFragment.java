package com.example.mohgggdraw;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/***
 This fragment handles notifications in the application. It:
 - Inflates the layout for displaying notifications
 - Sets up any necessary UI components or listeners
 ***/
public class NotificationFragment extends Fragment {
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<NotificationModel> notificationList;
    private FirebaseFirestore db;
    private String deviceId;
    private Boolean initialLoad;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Fetch the device ID dynamically
        deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e("NotificationFragment", "Device ID: " + deviceId);

        recyclerView = view.findViewById(R.id.recyclerViewNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(notificationList,
                this::handleDeclineAction, // Decline listener
                this::handleAcceptAction  // Accept listener
        );
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        initialLoad = true;
        loadNotifications();
    }

    public interface EventDetailsCallback {
        void onEventDetailsFetched(NotificationModel notification);
    }


    private void loadNotifications() {
        db.collection("notification")
                .whereEqualTo("deviceId", deviceId)
                .orderBy("created_at", Query.Direction.ASCENDING) // Order notifications by timestamp
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w("NotificationFragment", "Listen failed.", e);
                        return;
                    }

                    if (snapshots != null && !snapshots.isEmpty()) {
                        if (initialLoad) {
                            initialLoad = false;
                            notificationList.clear(); // Clear old data if necessary

                            // Load all notifications initially
                            for (QueryDocumentSnapshot doc : snapshots) {
                                NotificationModel notification = doc.toObject(NotificationModel.class);
                                fetchEventDetails(notification, updatedNotification -> {
                                    notificationList.add(updatedNotification);
                                    adapter.notifyDataSetChanged();
                                });
                            }
                        } else {
                            // Only handle the latest notification snapshot after initial load
                            DocumentSnapshot latestDoc = snapshots.getDocuments().get(snapshots.size() - 1);
                            NotificationModel newNotification = latestDoc.toObject(NotificationModel.class);

                            fetchEventDetails(newNotification, updatedNotification -> {
                                // Show the notification with event details
                                showNotification(
                                        getContext(),
                                        newNotification.getTitle(),
                                        newNotification.getMessage(),
                                        updatedNotification.getTitle(),
                                        updatedNotification.getStartTime()
                                );

                                // Add to notificationList and update the adapter
                                notificationList.add(updatedNotification);
                                adapter.notifyDataSetChanged();
                            });
                        }
                    }
                });
    }


    private void fetchEventDetails(NotificationModel notification, EventDetailsCallback callback) {
        String eventId = notification.getEventId();
        if (eventId == null || eventId.isEmpty()) {
            Log.w("NotificationFragment", "Notification has no event ID.");
            return;
        }

        db.collection("Events").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String eventName = documentSnapshot.getString("eventTitle");
                        String startTime = documentSnapshot.getString("startTime");
                        notification.setTitle(notification.getTitle());
                        notification.setEventDetail("Event: " + eventName + " starts at: " + startTime);
                        notification.setMessage(notification.getMessage());
                    } else {
                        notification.setMessage("Event details unavailable.");
                    }

                    // Notify the callback with the updated notification details
                    callback.onEventDetailsFetched(notification);
                })
                .addOnFailureListener(e -> {
                    Log.w("NotificationFragment", "Failed to fetch event details for event ID: " + eventId, e);
                });
    }


    private void handleDeclineAction(NotificationModel notification) {
        String eventId = notification.getEventId();
        String userId = deviceId; // Using deviceId as the user ID

        DocumentReference eventRef = db.collection("Events").document(eventId);
        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(eventRef);
            List<String> selectedList = (List<String>) snapshot.get("EventSelectedlist");
            List<String> cancelledList = (List<String>) snapshot.get("EventCancelledlist");

            if (selectedList != null && cancelledList != null) {
                // Check if user is already in cancelledList
                if (cancelledList.contains(userId)) {
                    return "already_declined";
                }

                // Remove user from selectedList and add to cancelledList
                selectedList.remove(userId);
                cancelledList.add(userId);

                transaction.update(eventRef, "EventSelectedlist", selectedList);
                transaction.update(eventRef, "EventCancelledlist", cancelledList);
            }
            return "decline_recorded";
        }).addOnSuccessListener(result -> {
            if ("already_declined".equals(result)) {
                Toast.makeText(getContext(), "Action already completed.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Decline action recorded successfully.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.w("NotificationFragment", "Error recording decline action", e);
        });
    }

    private void handleAcceptAction(NotificationModel notification) {
        String eventId = notification.getEventId();
        String userId = deviceId;

        DocumentReference eventRef = db.collection("Events").document(eventId);
        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(eventRef);
            List<String> selectedList = (List<String>) snapshot.get("EventSelectedlist");
            List<String> confirmedList = (List<String>) snapshot.get("EventConfirmedlist");

            if (selectedList != null && confirmedList != null) {
                if (confirmedList.contains(userId)) {
                    return "already_confirmed";
                }

                selectedList.remove(userId);
                confirmedList.add(userId);

                transaction.update(eventRef, "EventSelectedlist", selectedList);
                transaction.update(eventRef, "EventConfirmedlist", confirmedList);
            }
            return "accept_recorded";
        }).addOnSuccessListener(result -> {
            if ("already_confirmed".equals(result)) {
                Toast.makeText(getContext(), "Action already completed.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Accept action recorded successfully.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.w("NotificationFragment", "Error recording accept action", e);
        });
    }



    public void showNotification(Context context, String title, String message, String eventTitle, String startTime) {
        Log.e("showNotification: ", "it ran thru here");
        createNotificationChannel(context); // Ensure the channel is created first

        if (getContext() == null) {
            Log.e("NotificationFragment", "Context is null, cannot show notification.");
            return;
        }

        // Create PendingIntent to open the main activity
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );


        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notification_channel_id")
                .setSmallIcon(R.drawable.bell)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Check for notification permission (API 33 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.e("NotificationFragment", "Notification permission not granted.");
            return;
        }

        // Send the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        int notificationId = (int) System.currentTimeMillis(); // Ensure unique notification ID
        notificationManager.notify(notificationId, builder.build());
        Log.d("NotificationFragment", "Notification should now be visible.");
    }

    public void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notification Channel Name";
            String description = "Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notification_channel_id", name, importance);
            channel.setDescription(description);

            // Register the channel with the system
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

    }

}
