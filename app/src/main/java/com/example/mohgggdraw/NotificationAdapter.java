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

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private List<NotificationModel> notificationList;
    private DeclineActionListener declineActionListener;
    private FirebaseFirestore db; // Firebase instance to fetch event details dynamically

    public NotificationAdapter(List<NotificationModel> notificationList, DeclineActionListener declineActionListener) {
        this.notificationList = notificationList;
        this.declineActionListener = declineActionListener;
        this.db = FirebaseFirestore.getInstance(); // Initialize Firestore instance
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

        // Dynamically fetch event details and populate UI
        fetchEventDetails(notification.getEventId(), holder);

        // Show or hide buttons based on notification status
        if ("selected".equals(notification.getStatus())) {
            holder.acceptButton.setVisibility(View.VISIBLE);
            holder.declineButton.setVisibility(View.VISIBLE);

            // Handle decline button click with the listener
            holder.declineButton.setOnClickListener(v -> declineActionListener.onDecline(notification));
        } else {
            holder.acceptButton.setVisibility(View.GONE);
            holder.declineButton.setVisibility(View.GONE);
        }

        // Implement dropdown toggle functionality
        holder.itemView.setOnClickListener(v -> {
            if (holder.details.getVisibility() == View.GONE) {
                holder.details.setVisibility(View.VISIBLE);
                holder.poster.setVisibility(View.VISIBLE);
            } else {
                holder.details.setVisibility(View.GONE);
                holder.poster.setVisibility(View.GONE);
            }
        });
    }

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
                        String eventDetails = documentSnapshot.getString("details"); // Example field
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

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView title, details, message;
        ImageView poster;
        Button acceptButton, declineButton;

        public NotificationViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notification_title);
            message = itemView.findViewById(R.id.notification_message);

            details = itemView.findViewById(R.id.event_details);
            poster = itemView.findViewById(R.id.event_poster);
            acceptButton = itemView.findViewById(R.id.accept_button);
            declineButton = itemView.findViewById(R.id.decline_button);

            // Initially hide the event details and poster (collapsed state)
            details.setVisibility(View.GONE);
            poster.setVisibility(View.GONE);
        }
    }

    // Interface for the decline action
    public interface DeclineActionListener {
        void onDecline(NotificationModel notification);
    }
}
