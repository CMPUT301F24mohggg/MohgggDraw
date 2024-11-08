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

    /**
     * Constructs a new NotificationAdapter with the given notification list and action listeners.
     *
     * @param notificationList      List of NotificationModel objects to display.
     * @param declineActionListener Listener for decline button actions.
     * @param acceptActionListener  Listener for accept button actions.
     */
    public NotificationAdapter(List<NotificationModel> notificationList, DeclineActionListener declineActionListener, AcceptActionListener acceptActionListener) {
        this.notificationList = notificationList;
        this.declineActionListener = declineActionListener;
        this.acceptActionListener = acceptActionListener;
        this.db = FirebaseFirestore.getInstance();
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
        holder.title.setText(notification.getTitle() != null ? notification.getTitle() : "Notification");
        holder.message.setText(notification.getMessage());
        holder.details.setText(notification.getEventDetail());

        // Dynamically fetch event details and populate UI
        fetchEventDetails(notification.getEventId(), holder);

        // Show or hide buttons based on notification status
        if ("selected".equals(notification.getStatus())) {
            holder.acceptButton.setVisibility(View.VISIBLE);
            holder.declineButton.setVisibility(View.VISIBLE);

            // Handle decline button click with the listener
            holder.declineButton.setOnClickListener(v -> declineActionListener.onDecline(notification));
            holder.acceptButton.setOnClickListener(v -> acceptActionListener.onAccept(notification));
        } else {
            holder.acceptButton.setVisibility(View.GONE);
            holder.declineButton.setVisibility(View.GONE);
        }

    }


    /**
     * Fetches event details from Firestore and updates the UI with the event information.
     *
     * @param eventId The ID of the event to fetch details for.
     * @param holder  The ViewHolder containing views to update with event information.
     */
    private void fetchEventDetails(String eventId, NotificationViewHolder holder) {
        if (eventId == null || eventId.isEmpty()) {
            holder.details.setText("Event details unavailable.");
            holder.poster.setImageResource(R.drawable.imageplaceholder); // Placeholder image
            return;
        }

        db.collection("Events").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Populate the event details dynamically
                        String eventDetails = documentSnapshot.getString("eventDetail"); // Example field
                        String posterUrl = documentSnapshot.getString("imageUrl");  // Example field

                        // Set event details in TextView
                        holder.details.setText(eventDetails != null ? eventDetails : "No event details available.");

                        // Load event image into ImageView using Glide
                        if (posterUrl != null && !posterUrl.isEmpty()) {
                            Glide.with(holder.poster.getContext())
                                    .load(posterUrl)
                                    .placeholder(R.drawable.imageplaceholder)
                                    .into(holder.poster);
                        } else {
                            holder.poster.setImageResource(R.drawable.imageplaceholder);
                        }
                    } else {
                        holder.details.setText("Event details unavailable.");
                        holder.poster.setImageResource(R.drawable.imageplaceholder);
                    }
                })
                .addOnFailureListener(e -> {
                    holder.details.setText("Failed to fetch event details.");
                    holder.poster.setImageResource(R.drawable.imageplaceholder);
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
        TextView title, details, message;
        ImageView poster;
        Button acceptButton, declineButton;

        /**
         * Constructs a NotificationViewHolder and initializes its UI components.
         *
         * @param itemView The view representing an individual notification item.
         */
        public NotificationViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notification_title);
            message = itemView.findViewById(R.id.notification_message);

            details = itemView.findViewById(R.id.event_details);
            poster = itemView.findViewById(R.id.event_poster);
            acceptButton = itemView.findViewById(R.id.accept_button);
            declineButton = itemView.findViewById(R.id.decline_button);

            // Initially hide the event details and poster (collapsed state)
            poster.setVisibility(View.GONE);
        }
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
