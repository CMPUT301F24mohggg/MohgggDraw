package com.example.mohgggdraw;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
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

        // Set notification title
        holder.notificationTitle.setText(notification.getTitle() != null ? notification.getTitle() : "Notification");

        // Set placeholders for event title and time
        holder.eventTitle.setText(notification.getEventTitle() != null ? notification.getEventTitle() : "Loading...");
        holder.eventStartMonth.setText(notification.getStartMonth() != null ? notification.getStartMonth() : "--");
        holder.eventStartDate.setText(notification.getStartDate() != null ? notification.getStartDate() : "--");

        // Show or hide buttons based on the notification's current status
        if ("selected".equals(notification.getStatus())) {
            // Initially show buttons for "selected" status
            holder.acceptButton.setVisibility(View.VISIBLE);
            holder.declineButton.setVisibility(View.VISIBLE);
        } else {
            // Hide buttons for other statuses
            holder.acceptButton.setVisibility(View.GONE);
            holder.declineButton.setVisibility(View.GONE);
        }

        // Dynamically fetch event details to confirm button visibility
        fetchEventDetails(notification.getEventId(), notification, holder);

        // Handle button actions
        holder.acceptButton.setOnClickListener(v -> {
            holder.acceptButton.setVisibility(View.GONE);
            holder.declineButton.setVisibility(View.GONE);
            acceptActionListener.onAccept(notification);
        });

        holder.declineButton.setOnClickListener(v -> {
            holder.acceptButton.setVisibility(View.GONE);
            holder.declineButton.setVisibility(View.GONE);
            declineActionListener.onDecline(notification);
        });
    }

    /**
     * Fetches event details from Firestore and updates the notification and UI.
     *
     * @param eventId      The ID of the event to fetch.
     * @param notification The current notification model being processed.
     * @param holder       The ViewHolder containing the UI components to update.
     */
    private void fetchEventDetails(String eventId, NotificationModel notification, NotificationViewHolder holder) {
        if (eventId == null || eventId.isEmpty()) {
            holder.eventDescription.setText("Event details unavailable.");
            holder.eventPoster.setImageResource(R.drawable.imageplaceholder);
            return;
        }

        db.collection("Events").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> eventConfirmedList = (List<String>) documentSnapshot.get("EventConfirmedlist");
                        List<String> eventCanceledList = (List<String>) documentSnapshot.get("EventCancelledlist");

                        // Update visibility based on lists
                        boolean isAccepted = eventConfirmedList != null && eventConfirmedList.contains(deviceId);
                        boolean isDeclined = eventCanceledList != null && eventCanceledList.contains(deviceId);

                        if (isAccepted || isDeclined) {
                            holder.acceptButton.setVisibility(View.GONE);
                            holder.declineButton.setVisibility(View.GONE);
                        } else {
                            holder.acceptButton.setVisibility(View.VISIBLE);
                            holder.declineButton.setVisibility(View.VISIBLE);
                        }

                        // Update notification model for future reference
                        notification.setAccepted(isAccepted);
                        notification.setDeclined(isDeclined);

                        // Update other event details in the UI
                        holder.eventDescription.setText(documentSnapshot.getString("eventDetail") != null
                                ? documentSnapshot.getString("eventDetail")
                                : "No event details available.");

                        holder.eventTitle.setText(documentSnapshot.getString("eventTitle") != null
                                ? documentSnapshot.getString("eventTitle")
                                : "Untitled Event");

                        String startTime = documentSnapshot.getString("startTime");
                        if (startTime != null && !startTime.isEmpty()) {
                            String[] dateParts = startTime.split("/");
                            if (dateParts.length == 3) {
                                holder.eventStartMonth.setText(getShortMonth(Integer.parseInt(dateParts[1])));
                                holder.eventStartDate.setText(dateParts[0]);
                            } else {
                                holder.eventStartMonth.setText("N/A");
                                holder.eventStartDate.setText("N/A");
                            }
                        }

                        String posterUrl = documentSnapshot.getString("imageUrl");
                        if (posterUrl != null && !posterUrl.isEmpty()) {
                            Glide.with(holder.eventPoster.getContext())
                                    .load(posterUrl)
                                    .placeholder(R.drawable.imageplaceholder)
                                    .into(holder.eventPoster);
                        } else {
                            holder.eventPoster.setImageResource(R.drawable.imageplaceholder);
                        }
                    } else {
                        holder.eventDescription.setText("Event details unavailable.");
                        holder.eventPoster.setImageResource(R.drawable.imageplaceholder);
                    }
                })
                .addOnFailureListener(e -> {
                    holder.eventDescription.setText("Failed to fetch event details.");
                    holder.eventPoster.setImageResource(R.drawable.imageplaceholder);
                });
    }



    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    /**
     * ViewHolder class for each notification item.
     */
    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView notificationTitle, eventTitle, eventDescription, eventStartMonth, eventStartDate;
        ImageView eventPoster;
        Button acceptButton, declineButton;

        /**
         * Constructs a NotificationViewHolder and initializes its UI components.
         *
         * @param itemView The view representing an individual notification item.
         */
        public NotificationViewHolder(View itemView) {
            super(itemView);

            // Initialize views based on the XML
            notificationTitle = itemView.findViewById(R.id.notification_title);
            eventTitle = itemView.findViewById(R.id.tv_event_title);
            eventDescription = itemView.findViewById(R.id.event_description);
            eventStartMonth = itemView.findViewById(R.id.event_start_month);
            eventStartDate = itemView.findViewById(R.id.event_start_date);
            eventPoster = itemView.findViewById(R.id.event_poster);
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
