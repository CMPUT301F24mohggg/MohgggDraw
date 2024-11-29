package com.example.mohgggdraw;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;
/**
 * Adapter for displaying notifications in a RecyclerView.
 * Each notification item can include a title, message, event details, and action buttons
 * for accepting or declining the notification. This adapter supports fetching event details dynamically from Firestore.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private List<NotificationModel> notificationList;
    private DeclineActionListener declineActionListener;
    private AcceptActionListener acceptActionListener;
    private FirebaseFirestore db; // Firebase instance to fetch event details dynamically
    private String deviceId;

    /**
     * Constructs a new NotificationAdapter with the given notification list and action listeners.
     *
     * @param notificationList      List of NotificationModel objects to display.
     * @param declineActionListener Listener for decline button actions.
     * @param acceptActionListener  Listener for accept button actions.
     * @param deviceId              The current device ID for checking event participation.
     */
    public NotificationAdapter(List<NotificationModel> notificationList, DeclineActionListener declineActionListener, AcceptActionListener acceptActionListener, String deviceId) {
        this.notificationList = notificationList;
        this.declineActionListener = declineActionListener;
        this.acceptActionListener = acceptActionListener;
        this.db = FirebaseFirestore.getInstance();
        this.deviceId = deviceId; // Initialize deviceId
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationModel notification = notificationList.get(position);

        // Reset visibility of buttons and status
        holder.acceptButton.setVisibility(View.VISIBLE);
        holder.declineButton.setVisibility(View.VISIBLE);
        holder.eventStatus.setVisibility(View.GONE);

        // Set initial placeholders
        holder.notificationTitle.setText(notification.getTitle() != null ? notification.getTitle() : "Notification Title");
        holder.notificationMessage.setText(notification.getMessage() != null ? notification.getMessage() : "Notification Message");
        holder.eventTitle.setText(notification.getEventTitle() != null ? notification.getEventTitle() : "Loading...");
        holder.eventStartMonth.setText(notification.getStartMonth() != null ? notification.getStartMonth() : "--");
        holder.eventStartDate.setText(notification.getStartDate() != null ? notification.getStartDate() : "--");

        // Check the current status of the notification and update UI accordingly
        updateNotificationStatusUI(notification, holder);

        // Dynamically fetch event details
        fetchEventDetails(notification.getEventId(), notification, holder);

        // Handle accept button action
        holder.acceptButton.setOnClickListener(v -> {
            holder.acceptButton.setVisibility(View.GONE);
            holder.declineButton.setVisibility(View.GONE);
            acceptActionListener.onAccept(notification);

            // Update notification status
            notification.setStatus("ACCEPTED");
            holder.eventStatus.setText("You have joined!");
            holder.eventStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green_success));
            holder.eventStatus.setVisibility(View.VISIBLE);
        });

        // Handle decline button action
        holder.declineButton.setOnClickListener(v -> {
            holder.acceptButton.setVisibility(View.GONE);
            holder.declineButton.setVisibility(View.GONE);
            declineActionListener.onDecline(notification);

            // Update notification status
            notification.setStatus("DECLINED");
            holder.eventStatus.setText("You have declined/canceled!");
            holder.eventStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red_error));
            holder.eventStatus.setVisibility(View.VISIBLE);
        });
    }

    // Add this method to handle initial status display
    private void updateNotificationStatusUI(NotificationModel notification, NotificationViewHolder holder) {
        if ("ACCEPTED".equals(notification.getStatus())) {
            holder.acceptButton.setVisibility(View.GONE);
            holder.declineButton.setVisibility(View.GONE);
            holder.eventStatus.setText("You have joined!");
            holder.eventStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green_success));
            holder.eventStatus.setVisibility(View.VISIBLE);
        } else if ("DECLINED".equals(notification.getStatus())) {
            holder.acceptButton.setVisibility(View.GONE);
            holder.declineButton.setVisibility(View.GONE);
            holder.eventStatus.setText("You have declined/canceled!");
            holder.eventStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red_error));
            holder.eventStatus.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Fetches event details from Firestore and updates the notification and UI.
     *
     * @param eventId      The ID of the event to fetch.
     * @param notification The current notification model being processed.
     * @param holder       The ViewHolder containing the UI components to update.
     */
    private void fetchEventDetails(String eventId, NotificationModel notification, NotificationViewHolder holder) {
        // Check for invalid or empty event ID
        if (eventId == null || eventId.isEmpty()) {
            updateUIWithDefaultValues(holder, "Event details unavailable.");
            return;
        }

        // Retrieve event details from Firestore
        db.collection("Events").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Process event details
                        processEventDetails(documentSnapshot, notification, holder);
                    } else {
                        // Document does not exist
                        updateUIWithDefaultValues(holder, "Event details unavailable.");
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle fetch failure
                    updateUIWithDefaultValues(holder, "Failed to fetch event details.");

                    // Optional: Log the error for debugging
                    Log.e("NotificationAdapter", "Error fetching event details", e);
                });
    }

    /**
     * Processes the retrieved event details and updates the UI.
     *
     * @param documentSnapshot The Firestore document snapshot containing event details.
     * @param notification     The current notification model.
     * @param holder           The ViewHolder to update.
     */
    private void processEventDetails(DocumentSnapshot documentSnapshot, NotificationModel notification, NotificationViewHolder holder) {
        // Check and retrieve event participation lists
        List<String> eventConfirmedList = (List<String>) documentSnapshot.get("EventConfirmedlist");
        List<String> eventCanceledList = (List<String>) documentSnapshot.get("EventCancelledlist");

        // Determine user's current participation status
        boolean isAccepted = eventConfirmedList != null && eventConfirmedList.contains(deviceId);
        boolean isDeclined = eventCanceledList != null && eventCanceledList.contains(deviceId);

        // Update button visibility based on event status and notification status
        updateButtonVisibility(notification, holder, isAccepted, isDeclined);

        // Update notification model
        notification.setAccepted(isAccepted);
        notification.setDeclined(isDeclined);

        // Update event description
        String eventDescription = documentSnapshot.getString("eventDetail");
        holder.eventDescription.setText(eventDescription != null
                ? eventDescription
                : "No event details available.");

        // Update event title
        String eventTitle = documentSnapshot.getString("eventTitle");
        holder.eventTitle.setText(eventTitle != null
                ? eventTitle
                : "Untitled Event");

        // Update event date and time
        updateEventDateTime(documentSnapshot, holder);

    }

    /**
     * Updates button visibility based on event and notification status.
     *
     * @param notification The current notification model.
     * @param holder       The ViewHolder containing the buttons.
     * @param isAccepted   Whether the user has accepted the event.
     * @param isDeclined   Whether the user has declined the event.
     */
    private void updateButtonVisibility(NotificationModel notification, NotificationViewHolder holder,
                                        boolean isAccepted, boolean isDeclined) {
        if (notification.getStatus() == null || isAccepted || isDeclined) {
            // Hide buttons if no status or user has already responded
            holder.acceptButton.setVisibility(View.GONE);
            holder.declineButton.setVisibility(View.GONE);

            // Show the status based on the conditions
            if(notification.getStatus() != null) {
                if (isAccepted) {
                    holder.eventStatus.setText("You have joined!");
                    holder.eventStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green_success)); // Green color
                    holder.eventStatus.setVisibility(View.VISIBLE);
                } else if (isDeclined) {
                    holder.eventStatus.setText("You have declined/canceled!");
                    holder.eventStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red_error)); // Red color
                    holder.eventStatus.setVisibility(View.VISIBLE);
                }
            }
        } else if ("selected".equals(notification.getStatus())) {
            // Show buttons only for "selected" status and if not already responded
            holder.acceptButton.setVisibility(View.VISIBLE);
            holder.declineButton.setVisibility(View.VISIBLE);
        } else {
            // Hide buttons for other statuses
            holder.acceptButton.setVisibility(View.GONE);
            holder.declineButton.setVisibility(View.GONE);
        }
    }

    /**
     * Updates event date and time information in the UI.
     *
     * @param documentSnapshot The Firestore document snapshot.
     * @param holder           The ViewHolder to update.
     */
    private void updateEventDateTime(DocumentSnapshot documentSnapshot, NotificationViewHolder holder) {
        String startTime = documentSnapshot.getString("startTime");
        if (startTime != null && !startTime.isEmpty()) {
            String[] dateParts = startTime.split("/");
            if (dateParts.length == 3) {
                // Update month and date
                holder.eventStartMonth.setText(getShortMonth(Integer.parseInt(dateParts[1])));
                holder.eventStartDate.setText(dateParts[0]);
            } else {
                // Fallback if date format is incorrect
                holder.eventStartMonth.setText("N/A");
                holder.eventStartDate.setText("N/A");
            }
        } else {
            // No start time provided
            holder.eventStartMonth.setText("N/A");
            holder.eventStartDate.setText("N/A");
        }
    }


    /**
     * Updates UI with default values when event details cannot be retrieved.
     *
     * @param holder     The ViewHolder to update.
     * @param message    The message to display in the event description.
     */
    private void updateUIWithDefaultValues(NotificationViewHolder holder, String message) {
        // Set default text for description
        holder.eventDescription.setText(message);

        // Hide action buttons
        holder.acceptButton.setVisibility(View.GONE);
        holder.declineButton.setVisibility(View.GONE);

        // Reset other text fields
        holder.eventTitle.setText("Untitled Event");
        holder.eventStartMonth.setText("N/A");
        holder.eventStartDate.setText("N/A");
    }


    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    /**
     * ViewHolder class for each notification item.
     */
    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView notificationTitle, notificationMessage, eventTitle, eventDescription,
                eventStartMonth, eventStartDate, eventStatus;
        Button acceptButton, declineButton;

        /**
         * Constructs a NotificationViewHolder and initializes its UI components.
         *
         * @param itemView The view representing an individual notification item.
         */
        public NotificationViewHolder(View itemView) {
            super(itemView);

            // Initialize views based on the XML
            eventStatus = itemView.findViewById(R.id.event_status_text);
            notificationTitle = itemView.findViewById(R.id.notification_title);
            notificationMessage = itemView.findViewById(R.id.notification_message);
            eventTitle = itemView.findViewById(R.id.tv_event_title);
            eventDescription = itemView.findViewById(R.id.event_description);
            eventStartMonth = itemView.findViewById(R.id.event_start_month);
            eventStartDate = itemView.findViewById(R.id.event_start_date);
            acceptButton = itemView.findViewById(R.id.accept_button);
            declineButton = itemView.findViewById(R.id.decline_button);
        }
    }

    private String getShortMonth(int month) {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return (month >= 1 && month <= 12) ? months[month - 1] : "N/A";
    }

    /**
     * Interface for handling decline actions on notifications.
     */
    public interface DeclineActionListener {
        /**
         * Called when a notification is declined.
         *
         * @param notification The declined NotificationModel.
         */
        void onDecline(NotificationModel notification);
    }

    /**
     * Interface for handling accept actions on notifications.
     */
    public interface AcceptActionListener {
        /**
         * Called when a notification is accepted.
         *
         * @param notification The accepted NotificationModel.
         */
        void onAccept(NotificationModel notification);
    }
}
