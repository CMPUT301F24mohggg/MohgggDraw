package com.example.mohgggdraw;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/***
 * Fragment to view event and join waitlist
 ***/
public class WaitlistFragment extends Fragment {

    private Event event = new Event();//for running tests
    private User user = new User();
    private Bitmap bmp;
    private ImageView iv;
    private TextView joinButton;
    private HomeFragment home;

    TextView name;
    TextView time;
    TextView day;
    TextView capacity;
    TextView location;
    // = new Event("olKgM5GAgkLRUqo97eVS", "testname", "testname", "https://firebasestorage.googleapis.com/v0/b/mohgggdraw.appspot.com/o/event_images%2F1730963184849.jpg?alt=media&token=8c93f3c0-2e18-494a-95ec-a95b864ccdbd", "testname", "testname", "testname", "testname", "testname", true);



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the device ID and set it as the user's UID
        String deviceId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        user.setUid(deviceId);  // Setting the device ID as UID

        // Mainly for testing purposes as I can build objects from the arguments
        if (getArguments() != null) {
            Bundle args = getArguments();
            if (args.getBoolean("Geo")) {
                event.setGeolocation(true);
                user.setUid("geotest");
            } else {
                user.setUid("mewoowww normal");
                event.setGeolocation(false);
            }
            if (args.getBoolean("Org")) {
                event.setOrgID("Uaf");
                ArrayList<String> list = new ArrayList<>();
                list.add("meow1");
                list.add("meow2");
                list.add("meow3");
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
        if(home !=null) {
            name.setText(event.getTitle());
            time.setText(event.getTime());
            day.setText(event.getDate());
            capacity.setText(String.valueOf(event.getMaxCapacity()));
            location.setText(event.getLocation());

            // Pulling and creating image
            iv = view.findViewById(R.id.organizer_event_poster);
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

            // Logic to decide if button is join, leave, or view waitlist
            joinButton = view.findViewById(R.id.eventInfoButton);
            if (Objects.equals(event.getOrgID(), "Uaf")) {
                // If organizer, view waitlist
                joinButton.setText("View waitlist");
                joinButton.setOnClickListener(v -> home.goToWaitlistView(event));
            } else {
                updateJoinButton();
            }
        }
    }

    public void setImportant(Event event, HomeFragment home){
        this.event = event;
        this.home = home;
    }

    private void updateJoinButton() {
        if (event.getWaitingList().contains(user.getUid())) {
            // If in event, leave waitlist
            joinButton.setText("Leave event");
            joinButton.setOnClickListener(v -> new leaveEventButton(event, user, this).show(getActivity().getSupportFragmentManager(), "join"));
        } else {
            // If not in waitlist, join
            joinButton.setText("Join event");
            joinButton.setOnClickListener(v -> {
                // Logic for geolocation
                if (event.isGeolocation()) {
                    new JoinWaitlistButton(event, user, this).show(getActivity().getSupportFragmentManager(), "join");
                } else {
                    new WaitinglistController(event).addUser(user);
                    onDialogueFinished();
                }
            });
        }
    }

    public void onDialogueFinished() {
        // Refresh page after dialogue to update the buttons
        updateJoinButton();
    }

    public Event getEvent() {
        return event;
    }

    public User getUser() {
        return user;
    }
}
