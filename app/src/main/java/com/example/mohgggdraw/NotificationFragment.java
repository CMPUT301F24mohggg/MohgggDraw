package com.example.mohgggdraw;

import android.app.Notification;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<NotificationModel> notificationList;
    private FirebaseFirestore db;
    private String deviceId = "c7dc1ae4a545d5a2"; // Replace with actual device ID logic

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(notificationList, this::handleDeclineAction); // Passing the decline handler
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadNotifications();
    }

    private void loadNotifications() {
        db.collection("notification")
                .whereEqualTo("deviceId", deviceId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("NotificationFragment", "Listen failed.", e);
                            return;
                        }

                        if (snapshots != null) {
                            notificationList.clear(); // Clear old data
                            for (QueryDocumentSnapshot doc : snapshots) {
                                NotificationModel notification = doc.toObject(NotificationModel.class);
                                notificationList.add(notification);
                            }
                            adapter.notifyDataSetChanged(); // Refresh RecyclerView
                        }
                    }
                });
    }

    private void handleDeclineAction(NotificationModel notification) {
        String eventId = notification.getEventId();
        // Implement how to fetch own device id
        String userId = notification.getEventId(); // Assuming NotificationModel has userId

        DocumentReference eventRef = db.collection("Events").document(eventId);
        db.runTransaction(transaction -> {
            // Fetch the current data of the event
            List<String> selectedList = (List<String>) transaction.get(eventRef).get("selectedList");
            List<String> declinedList = (List<String>) transaction.get(eventRef).get("declinedList");

            if (selectedList != null && declinedList != null) {
                selectedList.remove(userId); // Remove user from selectedList
                declinedList.add(userId);    // Add user to declinedList

                // Update Firestore with new lists
                transaction.update(eventRef, "selectedList", selectedList);
                transaction.update(eventRef, "declinedList", declinedList);
            }
            return null;
        }).addOnSuccessListener(aVoid -> {
            Log.d("NotificationFragment", "Decline action recorded successfully.");
        }).addOnFailureListener(e -> {
            Log.w("NotificationFragment", "Error recording decline action", e);
        });

        db.collection("user").document(deviceID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Device ID exists, let user enter the app
                Toast.makeText(MainActivity.this, "Welcome back!", Toast.LENGTH_SHORT).show();
                navigateToHomeScreen();
            } else {
                // Device ID does not exist, show signup option
                signupLayout.setVisibility(View.VISIBLE);
                buttonSignup.setOnClickListener(view -> {
                    Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                    startActivity(intent);
                });
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(MainActivity.this, "Failed to check device ID: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("MainActivity", "Failed to check device ID: " + e.getMessage());
        });
    }
}
