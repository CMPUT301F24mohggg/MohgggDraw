package com.example.mohgggdraw;

import android.content.Intent;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/***
 * Fragment to view event and join waitlist
 * ***/
public class WaitlistFragment extends Fragment {
    Event event = new Event("olKgM5GAgkLRUqo97eVS","testname","testname","https://firebasestorage.googleapis.com/v0/b/mohgggdraw.appspot.com/o/event_images%2F1730963184849.jpg?alt=media&token=8c93f3c0-2e18-494a-95ec-a95b864ccdbd","testname","testname","testname","testname","testname",true);
    User user = new User();
    String path;
    Bitmap bmp;
    ImageView iv;
    StorageReference storageReference;
    private ViewPager2 viewPager2;
    TextView joinButton;
    HomeFragment home;


    public WaitlistFragment(){
        super();
    }



    public WaitlistFragment(Event event, User user,HomeFragment home) {
        super();
        this.event=event;
        this.user = user;
        this.home = home;


    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the device ID and set it as the user's UID
        String deviceId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        user.setUid(deviceId);  // Setting the device ID as Uid

        //mainly for testing purpose as i can build objects from the arguments
        if(getArguments() != null) {
            Bundle args = getArguments();
            if (getArguments().getBoolean("Geo")) {
                event.setGeolocation(true);
                user.setUid("geotest");
            } else if (getArguments().getBoolean("Geo") == false) {
                user.setUid("mewoowww normal");
                event.setGeolocation(false);
            }
            if (getArguments().getBoolean("Org")){
                event.setOrgID("Uaf");
                ArrayList<String> list = new ArrayList<String>();
                list.add("meow1");
                list.add("meow2");
                list.add("meow3");
                event.setWaitingList(list);
            }
        }


        return inflater.inflate((R.layout.view_event), container, false);
    }

        public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
            super.onViewCreated(view, savedInstanceState);

            viewPager2 = view.findViewById(R.id.view_pager);

            TextView name = (TextView) view.findViewById(R.id.eventtitle);
            TextView time = (TextView) view.findViewById(R.id.eventInfoTime);
            TextView day = (TextView) view.findViewById(R.id.eventInfoDay);
            TextView capacity = (TextView) view.findViewById(R.id.eventInfoPeople);
            TextView location = (TextView) view.findViewById(R.id.eventInfoLocation);


            name.setText(event.getTitle());
            time.setText(event.getTime());
            day.setText(event.getDate());
            capacity.setText(String.valueOf(event.getMaxCapacity()));
            location.setText(event.getLocation());

// pulling and creating image
            path = event.getPosterUrl();
            iv = (ImageView) view.findViewById(R.id.eventimage);
            StorageReference myimage = new WaitinglistDB().getImage(path);
            try{
                File eventImage = File.createTempFile(event.getTitle(),".png");
                myimage.getFile(eventImage)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Bitmap bitmap = BitmapFactory.decodeFile(eventImage.getAbsolutePath());
                                iv.setImageBitmap(bitmap);

                            }
                        });{

                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
// logic to decide if button is join leave or view waitlist
            joinButton = view.findViewById(R.id.eventInfoButton);
            if(Objects.equals(event.getOrgID(), "Uaf")){
                //if organizer view waitlist
                joinButton.setText("View waitlist");
                joinButton.setOnClickListener(v -> {
                    home.goToWaitlistView(event);

            });
            }else {

                if (event.getWaitingList().contains(user.getUid())) {
                    //if in event leave waitlist
                    joinButton.setText("Leave event");
                    joinButton.setOnClickListener(v -> {
                                new leaveEventButton(event, user, this).show(getActivity().getSupportFragmentManager(), "join");
                            }
                    );


                } else {
                    //if not in waitlist join
                    joinButton.setText("Join event");
                    joinButton.setOnClickListener(v -> {

                        //logic for geolocation
                        if (event.isGeolocation()) {
                            new JoinWaitlistButton(event, user, this).show(getActivity().getSupportFragmentManager(), "join");
                        } else {
                            new WaitinglistController(event).addUser(user);
                            onDialogueFinished();

                        }
                    });
                }
            }
        }


    public void onDialogueFinished(){
        //refresh page after dialogue to update teh buttons
        if (event.getWaitingList().contains(user.getUid())){
            joinButton.setText("Leave event");
            joinButton.setOnClickListener(v->{
                        new leaveEventButton(event, user,this).show(getActivity().getSupportFragmentManager(),"join");
                    }
            );


        }else {
            joinButton.setText("Join event");
            joinButton.setOnClickListener(v -> {

                if (event.isGeolocation()) {
                    new JoinWaitlistButton(event, user, this).show(getActivity().getSupportFragmentManager(), "join");
                } else {
                    new WaitinglistController(event).addUser(user);
                    onDialogueFinished();

                }
            });
        }



    }
    public Event getEvent(){
        return event;
    }
    public User getUser(){
        return user;
    }
}
