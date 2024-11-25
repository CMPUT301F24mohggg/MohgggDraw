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
        holder.notificationTitle.setText(notification.getTitle() != null ? notification.getTitle() : "Notification");

        // Set placeholders for event title and time
        holder.eventTitle.setText(notification.getEventTitle() != null ? notification.getEventTitle() : "Loading...");
        holder.eventStartMonth.setText(notification.getStartMonth() != null ? notification.getStartMonth() : "--");
        holder.eventStartDate.setText(notification.getStartDate() != null ? notification.getStartDate() : "--");

        // Dynamically fetch event details and populate UI
        fetchEventDetails(notification.getEventId(), holder);

        // Show or hide buttons based on notification status
        if ("selected".equals(notification.getStatus())) {
            holder.acceptButton.setVisibility(View.VISIBLE);
            holder.declineButton.setVisibility(View.VISIBLE);

            // Handle decline button click with the listener
            holder.declineButton.setOnClickListener(v -> {
                // Hide the buttons after clicking decline
                holder.acceptButton.setVisibility(View.GONE);
                holder.declineButton.setVisibility(View.GONE);

                // Notify the DeclineActionListener
                declineActionListener.onDecline(notification);

                // Optional: Show a toast message for confirmation
                Toast.makeText(holder.itemView.getContext(), "You have ", Toast.LENGTH_SHORT).show();
            });

            // Handle accept button click with the listener
            holder.acceptButton.setOnClickListener(v -> {
                // Hide the buttons after clicking accept
                holder.acceptButton.setVisibility(View.GONE);
                holder.declineButton.setVisibility(View.GONE);

                // Notify the AcceptActionListener
                acceptActionListener.onAccept(notification);

            });
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
            holder.eventDescription.setText("Event details unavailable.");
            holder.eventPoster.setImageResource(R.drawable.imageplaceholder); // Placeholder image
            return;
        }

        db.collection("Events").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String eventDetails = documentSnapshot.getString("eventDetail");
                        String posterUrl = documentSnapshot.getString("imageUrl");
                        String startTime = documentSnapshot.getString("startTime");
                        String eventTitle = documentSnapshot.getString("eventTitle");

                        // Set event details
                        holder.eventDescription.setText(eventDetails != null ? eventDetails : "No event details available.");

                        // Set event title
                        holder.eventTitle.setText(eventTitle != null ? eventTitle : "Untitled Event");

                        // Set start date and month
                        if (startTime != null && !startTime.isEmpty()) {
                            String[] dateParts = startTime.split("/"); // Assuming format is "DD/MM/YYYY"
                            if (dateParts.length == 3) {
                                holder.eventStartMonth.setText(getShortMonth(Integer.parseInt(dateParts[1])));
                                holder.eventStartDate.setText(dateParts[0]);
                            } else {
                                holder.eventStartMonth.setText("N/A");
                                holder.eventStartDate.setText("N/A");
                            }
                        } else {
                            holder.eventStartMonth.setText("N/A");
                            holder.eventStartDate.setText("N/A");
                        }

                        // Load event poster image
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
