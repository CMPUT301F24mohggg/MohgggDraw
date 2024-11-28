package com.example.mohgggdraw;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NotificationFragment extends Fragment {
    private static final String TAG = "NotificationFragment";

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<NotificationModel> notificationList;
    private FirebaseFirestore db;
    private String deviceId;
    private Boolean initialLoad;
    private Context preContext = null;
    private Context actualContext;
    private ProgressBar loadingProgressBar;
    private TextView emptyStateTextView;
    private Boolean inFragment = false;

    /**
     * Inflates the fragment's view and initializes the RecyclerView and Firestore instance.
     * Also fetches the device ID dynamically to fetch notifications related to this device.
     *
     * @param inflater The LayoutInflater object to inflate the view.
     * @param container The parent view that the fragment's UI will be attached to.
     * @param savedInstanceState A Bundle containing the activity's previously saved state.
     *
     * @return The root view of the fragment's layout.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        // Initialize views
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar);
        emptyStateTextView = view.findViewById(R.id.emptyStateTextView);
        return view;
    }


    /**
     * Called when the view for the fragment has been created. Sets up the RecyclerView with
     * the adapter and starts loading notifications from Firestore.
     *
     * @param view The root view of the fragment's layout.
     * @param savedInstanceState A Bundle containing the fragment's previously saved state.
     */
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Fetch the device ID dynamically
        deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e("NotificationFragment", "Device ID: " + deviceId);

        recyclerView = view.findViewById(R.id.recyclerViewNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        inFragment = true;
        adapter = new NotificationAdapter(notificationList,
                this::handleDeclineAction, // Decline listener
                this::handleAcceptAction,  // Accept listener
                deviceId

        );
        recyclerView.setAdapter(adapter);
    }

    /**
     * Pre-loads notifications in the background.
     * This method can be called during app initialization to prepare notifications.
     */
    public void preLoadNotifications(Context context) {
        if (context == null) {
            Log.e(TAG, "Context is null, cannot preload notifications");
            return;
        }

        if (preContext == null) {
            preContext = context;
        }

        // Ensure lists and adapter are initialized
        if (notificationList == null) {
            notificationList = new ArrayList<>();
        }

        if (adapter == null) {
            adapter = new NotificationAdapter(
                    notificationList,
                    this::handleDeclineAction,
                    this::handleAcceptAction,
                    deviceId
            );

            if (recyclerView != null) {
                recyclerView.setAdapter(adapter);
            }
        }

        deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        db = FirebaseFirestore.getInstance();

        initialLoad = true;
        loadNotifications();
    }


    /**
     * Loads notifications from Firestore and listens for updates in real-time.
     * Notifications are filtered by device ID and ordered by the creation timestamp.
     * On the first load, it fetches all notifications; subsequent updates only fetch the latest ones.
     */
    private void loadNotifications() {
        if (getContext() == null) {
            actualContext = preContext;
        } else {
            actualContext = getContext();
        }

        // Show loading progress
        if(inFragment) {
            loadingProgressBar.setVisibility(View.VISIBLE);
            emptyStateTextView.setVisibility(View.GONE);
        }
        db.collection("notification")
                .orderBy("created_at", Query.Direction.DESCENDING)
                .whereEqualTo("deviceId", deviceId)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        updateUIState();
                        return;
                    }

                    if (snapshots != null && !snapshots.isEmpty()) {
                        if (initialLoad) {
                            initialLoad = false;
                            notificationList.clear();

                            for (QueryDocumentSnapshot doc : snapshots) {
                                NotificationModel notification = doc.toObject(NotificationModel.class);

                                // Only add to in-app notifications if status is not null
                                if (notification.getStatus() != null) {
                                    fetchEventDetails(notification, updatedNotification -> {
                                        notificationList.add(updatedNotification);
                                        adapter.notifyDataSetChanged();
                                    });
                                } else {
                                    // For notifications with null status, only show system notification
                                    fetchEventDetails(notification, updatedNotification -> {
                                        showNotification(
                                                actualContext,
                                                updatedNotification.getTitle(),
                                                updatedNotification.getMessage(),
                                                updatedNotification.getTitle(),
                                                updatedNotification.getStartTime()
                                        );
                                    });
                                }
                            }
                        } else {
                            DocumentSnapshot latestDoc = snapshots.getDocuments().get(0);
                            NotificationModel newNotification = latestDoc.toObject(NotificationModel.class);

                            boolean isDuplicate = false;
                            for (NotificationModel existingNotification : notificationList) {
                                if (existingNotification.equals(newNotification)) {
                                    isDuplicate = true;
                                    break;
                                }
                            }

                            if (!isDuplicate) {
                                fetchEventDetails(newNotification, updatedNotification -> {
                                    // Only add to in-app notifications if status is not null
                                    if (updatedNotification.getStatus() != null) {
                                        notificationList.add(0, updatedNotification);
                                        adapter.notifyItemInserted(0);
                                    }

                                    // Always show system notification for new notifications
                                    showNotification(
                                            actualContext,
                                            updatedNotification.getTitle(),
                                            updatedNotification.getMessage(),
                                            updatedNotification.getTitle(),
                                            updatedNotification.getStartTime()
                                    );
                                });
                            }
                        }
                    }
                    if(inFragment) {
                        updateUIState();
                    }
                });
    }

    private void updateUIState() {
        loadingProgressBar.setVisibility(View.GONE);

        if (notificationList == null || notificationList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyStateTextView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateTextView.setVisibility(View.GONE);
        }
    }

    /**
     * Fetches event details from the Firestore database for a specific notification.
     * Updates the notification with event details such as the event title and start time.
     *
     * @param notification The notification object to be updated with event details.
     * @param callback The callback to notify when event details are fetched.
     */
    private void fetchEventDetails(NotificationModel notification, EventDetailsCallback callback) {
        String eventId = notification.getEventId();
        if (eventId == null || eventId.isEmpty()) {
            Log.w(TAG, "Notification has no event ID.");
            callback.onEventDetailsFetched(notification);
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
                        notification.setStartTime(startTime);
                    } else {
                        notification.setMessage("Event details unavailable.");
                    }

                    // Notify the callback with the updated notification details
                    callback.onEventDetailsFetched(notification);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Failed to fetch event details for event ID: " + eventId, e);
                    callback.onEventDetailsFetched(notification);
                });
    }



    /**
     * Handles the action when the user declines an event notification.
     * It removes the user from the selected list and adds them to the cancelled list in Firestore.
     *
     * @param notification The notification representing the event the user wants to decline.
     */
    private void handleDeclineAction(NotificationModel notification) {
        String eventId = notification.getEventId();
        String userId = deviceId;

        DocumentReference eventRef = db.collection("Events").document(eventId);
        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(eventRef);

            List<String> selectedList = (List<String>) snapshot.get("EventSelectedlist");
            List<String> cancelledList = (List<String>) snapshot.get("EventCancelledlist");

            // Ensure the lists are not null
            if (selectedList == null) selectedList = new ArrayList<>();
            if (cancelledList == null) cancelledList = new ArrayList<>();

            if (cancelledList.contains(userId)) {
                return "already_declined";
            }

            // Remove from selected and add to cancelled
            selectedList.remove(userId);
            cancelledList.add(userId);

            transaction.update(eventRef, "EventSelectedlist", selectedList);
            transaction.update(eventRef, "EventCancelledlist", cancelledList);

            return "decline_recorded";
        }).addOnSuccessListener(result -> {
            if ("already_declined".equals(result)) {
                Toast.makeText(getContext(), "Action already completed.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Decline action recorded successfully.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.w(TAG, "Error recording decline action", e);
        });
    }



    /**
     * Handles the action when the user accepts an event notification.
     * It removes the user from the selected list and adds them to the confirmed list in Firestore.
     *
     * @param notification The notification representing the event the user wants to accept.
     */
    private void handleAcceptAction(NotificationModel notification) {
        String eventId = notification.getEventId();
        String userId = deviceId;

        DocumentReference eventRef = db.collection("Events").document(eventId);
        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(eventRef);

            List<String> selectedList = (List<String>) snapshot.get("EventSelectedlist");
            List<String> confirmedList = (List<String>) snapshot.get("EventConfirmedlist");

            // Ensure the lists are not null
            if (selectedList == null) selectedList = new ArrayList<>();
            if (confirmedList == null) confirmedList = new ArrayList<>();

            if (confirmedList.contains(userId)) {
                return "already_confirmed";
            }

            // Remove from selected and add to confirmed
            selectedList.remove(userId);
            confirmedList.add(userId);

            transaction.update(eventRef, "EventSelectedlist", selectedList);
            transaction.update(eventRef, "EventConfirmedlist", confirmedList);

            return "accept_recorded";
        }).addOnSuccessListener(result -> {
            if ("already_confirmed".equals(result)) {
                Toast.makeText(getContext(), "Action already completed.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Accept action recorded successfully.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.w(TAG, "Error recording accept action", e);
        });
    }



    /**
     * Displays a local notification for an event, including the event's title, message, and start time.
     *
     * @param context The context used to display the notification.
     * @param title The title of the notification.
     * @param message The message of the notification.
     * @param eventTitle The title of the event.
     * @param startTime The start time of the event.
     */
    public void showNotification(Context context, String title, String message, String eventTitle, String startTime) {
        if (context == null) {
            Log.e(TAG, "Context is null, cannot show notification.");
            return;
        }

        createNotificationChannel(context);

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
            Log.e(TAG, "Notification permission not granted.");
            return;
        }

        // Send the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        int notificationId = (int) System.currentTimeMillis(); // Ensure unique notification ID
        notificationManager.notify(notificationId, builder.build());
        Log.d("NotificationFragment", "Notification should now be visible.");
    }

    /**
     * Creates the notification channel required for displaying notifications on Android O and above.
     *
     * @param context The context used to create the notification channel.
     */
    public void createNotificationChannel(Context context) {
        if (context == null) {
            Log.e(TAG, "Context is null. Cannot create notification channel.");
            return;
        }
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

    /**
     * Callback interface for fetching event details.
     */
    public interface EventDetailsCallback {
        /**
         * Called when event details have been fetched and the notification has been updated.
         *
         * @param notification The updated notification with event details.
         */
        void onEventDetailsFetched(NotificationModel notification);
    }
}