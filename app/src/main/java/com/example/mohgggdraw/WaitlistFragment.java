package com.example.mohgggdraw;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class WaitlistFragment extends Fragment {
    Event event = new Event("olKgM5GAgkLRUqo97eVS","testname","testname","https://firebasestorage.googleapis.com/v0/b/mohgggdraw.appspot.com/o/event_images%2F1730963184849.jpg?alt=media&token=8c93f3c0-2e18-494a-95ec-a95b864ccdbd","testname","testname","testname","testname","testname",true);
    User user = new User();
    String path;
    Bitmap bmp;
    ImageView iv;
    StorageReference storageReference;


    public WaitlistFragment(){
        super();
    }



    public WaitlistFragment(Event event) {
        super();
        this.event=event;


    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            if (getArguments().getBoolean("geo")) {
                event.setGeolocation(true);
                user.setEmail("geotest");
            } else if (getArguments().getBoolean("geo") == false) {
                user.setEmail("mewoowww normal");


            }
        }
        event.setGeolocation(false);

        return inflater.inflate((R.layout.view_event), container, false);
    }

        public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
            super.onViewCreated(view, savedInstanceState);



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


            path = event.getPosterUrl();
            iv = (ImageView) view.findViewById(R.id.eventimage);
            StorageReference myimage = new WaitinglistDB(event).getImage(path);
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

            TextView joinButton = view.findViewById(R.id.eventInfoButton);

            if (event.getWaitingList().contains(user.getEmail())){
                joinButton.setText("Leave event");
                joinButton.setOnClickListener(v->{
                            new leaveEventButton(event, user,this).show(getActivity().getSupportFragmentManager(),"join");
                        }
                        );


            }else {
                joinButton.setText("Join Event");
                joinButton.setOnClickListener(v -> {

                    if (event.isGeolocation()) {
                        new JoinWaitlistButton(event, user,this).show(getActivity().getSupportFragmentManager(), "join");
                    } else {
                        new WaitinglistController(user,event).addUser(user);
                        onDialogueFinished();

                    }
                });
            }


        }


    public void onDialogueFinished(){
        Fragment fragment = new WaitlistFragment(event);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.show(fragment).commit();

    }
    public Event getEvent(){
        return event;
    }
    public User getUser(){
        return user;
    }
}
