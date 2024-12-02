package com.example.mohgggdraw;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Fragment to display event details and allow users to join or leave the event waitlist.
 * Organizers can view the waitlist and send notifications to participants.
 */
public class WaitlistFragment extends Fragment {
    private Event event;
    private User user;
    private Bitmap bmp;
    private ImageView iv;
    private TextView joinButton;
    private ConstraintLayout sendNotificationButton;
    private HomeFragment home;
    private String deviceId;

    // TextViews for event details
    private TextView name;
    private TextView time;
    private TextView day;
    private TextView capacity;
    private TextView location;

    /**
     * Default constructor. Initializes with default `Event` and `User` objects.
     */
    public WaitlistFragment() {
        super();
        this.event = new Event();
        this.user = new User();
    }

    // Constructor with event, user, and home fragment
    public WaitlistFragment(Event event, User user, HomeFragment home) {
        super();
        this.event = event;
        this.user = user;
        this.home = home;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the device ID and set it as the user's UID
        deviceId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        user.setUid(deviceId);

        // Handle test arguments if needed
        if (getArguments() != null) {
            Bundle args = getArguments();
            if (args.getBoolean("Geo")) {
                event.setGeolocation(true);
                user.setUid("geotest");
            } else {
                user.setUid("device_user");
                event.setGeolocation(false);
            }

            if (args.getBoolean("Org")) {
                event.setOrgID(deviceId);
                ArrayList<String> list = new ArrayList<>();
                list.add("test_user1");
                list.add("test_user2");
                list.add("test_user3");
                event.setWaitingList(list);
            }
        }

        return inflater.inflate(R.layout.view_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        name = view.findViewById(R.id.eventtitle);
        time = view.findViewById(R.id.eventInfoTime);
        day = view.findViewById(R.id.eventInfoDay);
        capacity = view.findViewById(R.id.eventInfoPeople);
        location = view.findViewById(R.id.eventInfoLocation);
        initializeViews(view);

        if (event != null) {
            name.setText(event.getTitle());
            time.setText(event.getStartTime().toString());
            day.setText(event.getDate());
            capacity.setText(String.valueOf(event.getMaxCapacity()));
            location.setText(event.getLocation());



            // Pulling and creating image
            iv = view.findViewById(R.id.eventimage);
            if (iv != null) {
                StorageReference myImage = new WaitinglistDB().getImage(event.getPosterUrl());
                try {
                    File eventImage = File.createTempFile(event.getTitle(), ".png");
                    myImage.getFile(eventImage).addOnSuccessListener(taskSnapshot -> {
                        Bitmap bitmap = BitmapFactory.decodeFile(eventImage.getAbsolutePath());
                        iv.setImageBitmap(bitmap);
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                // Log or handle the case where ImageView is null
                System.err.println("ImageView is null, make sure the layout contains the correct ID.");
            }

            // Load event image
            loadEventImage();

            // Set up join/leave button logic
            setupJoinButton();
        }
    }

        /**
         * Initializes the views in the layout.
         *
         * @param view The root view of the fragment.
         */
        private void initializeViews (View view){
            name = view.findViewById(R.id.eventtitle);
            time = view.findViewById(R.id.eventInfoTime);
            day = view.findViewById(R.id.eventInfoDay);
            capacity = view.findViewById(R.id.eventInfoPeople);
            location = view.findViewById(R.id.eventInfoLocation);
            iv = view.findViewById(R.id.eventimage);
            joinButton = view.findViewById(R.id.eventInfoButton);
            sendNotificationButton = view.findViewById(R.id.send_notification_button);
        }

        /**
         * Populates the event details in the UI.
         */
        private void setEventDetails () {
            name.setText(event.getTitle());
            time.setText(event.getTime());
            day.setText(event.getDate());
            capacity.setText(String.valueOf(event.getMaxCapacity()));
            location.setText(event.getLocation());
        }


        /**
         * Downloads and displays the event's poster image if available.
         */
        private void loadEventImage () {
            if (event.getPosterUrl() != null) {
                StorageReference myImage = new WaitinglistDB().getImage(event.getPosterUrl());
                try {
                    File eventImage = File.createTempFile(event.getTitle(), ".png");
                    myImage.getFile(eventImage)
                            .addOnSuccessListener(taskSnapshot -> {
                                Bitmap bitmap = BitmapFactory.decodeFile(eventImage.getAbsolutePath());
                                iv.setImageBitmap(bitmap);
                            });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        /**
         * Sets up the join/leave button logic based on the user's role and event status.
         */
        private void setupJoinButton () {
            if (Objects.equals(event.getOrgID(), deviceId)) {
                // If organizer, view waitlist
                setupOrganizerButton();
            } else {
                updateJoinButton();
            }
        }

        /**
         * Configures the button for organizers to view the waitlist and send notifications.
         */
        private void setupOrganizerButton () {
            joinButton.setText("View waitlist");
            joinButton.setOnClickListener(v -> {
                if (home != null) home.goToWaitlistView(event);
            });

            if (sendNotificationButton != null) {
                sendNotificationButton.setVisibility(View.VISIBLE);
                sendNotificationButton.setOnClickListener(v -> {
                    if (home != null) home.goToSendNotificationView(event);
                });
            }
        }

        /**
         * Updates the join button text and functionality for regular users.
         */
        private void updateJoinButton () {
            if (event.getWaitingList().contains(user.getUid())) {
                // If in event, leave waitlist
                joinButton.setText("Leave event");
                joinButton.setOnClickListener(v ->
                        new leaveEventButton(event, user, this).show(getActivity().getSupportFragmentManager(), "join")
                );
            } else {
                // If not in waitlist, join
                joinButton.setText("Join event");
                joinButton.setOnClickListener(v -> {
                    // Logic for geolocation
                    if (event.isGeolocation()) {
                        new JoinWaitlistButton(event, user, this).show(getActivity().getSupportFragmentManager(), "join");
                    } else {
                        new WaitinglistController(event).addUser(user);
                        new UserDB().addEventToUserList(event.getEventId(), deviceId);
                        onDialogueFinished();
                    }
                });
            }
        }

        /**
         * Refreshes the page after an action to update the join button state.
         */
        public void onDialogueFinished () {
            // Refresh page after dialogue to update the buttons
            updateJoinButton();
        }

        // Utility methods for testing and configuration

        /**
         * Sets the event and home fragment for navigation and data.
         *
         * @param event The event object.
         * @param home  The home fragment.
         */
        public void setImportant (Event event, HomeFragment home){
            this.event = event;
            this.home = home;
        }

        public void setDeviceId (String deviceId){
            this.deviceId = deviceId;
        }

        /**
         * Retrieves the associated event.
         *
         * @return The event object.
         */
        public Event getEvent () {
            return event;
        }
        /**
         * Retrieves the user object.
         *
         * @return The user object.
         */
        public User getUser () {
            return user;
        }
    }
