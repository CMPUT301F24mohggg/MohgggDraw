package com.example.mohgggdraw;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class WaitlistFragment extends AppCompatActivity {
    Event event;
    User user;
    String path;
    Bitmap bmp;
    ImageView iv;
    StorageReference storageReference;

    public WaitlistFragment() {
        super();


    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_event);

        Intent intent = getIntent();
        event = (Event) intent.getExtras().getSerializable("event");
        user = (User) intent.getExtras().getSerializable("user");

        TextView name = (TextView) findViewById(R.id.eventInfoTime);
        name.setText(event.getName());
        path = event.getPath();
        iv = (ImageView) findViewById(R.id.eventimage);
        StorageReference myimage = new WaitinglistDB(event).getImage();
        try{
            File eventImage = File.createTempFile(event.getName(),".png");
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



        TextView joinButton = findViewById(R.id.eventInfoButton);

        if (event.getWaitingList().contains(user.getEmail())){
            joinButton.setText("leaveaf");
            joinButton.setOnClickListener(v->{
                        new leaveEventButton(event, user,this).show(getSupportFragmentManager(),"join");
                    }
                    );


        }else {
            joinButton.setOnClickListener(v -> {
                if (event.hasGeolocation()) {
                    new JoinWaitlistButton(event, user,this).show(getSupportFragmentManager(), "join");
                } else {
                    new WaitinglistDB(event).addToDB(user);

                }
            });
        }


    }


    public void onDialogueFinished(Event event, User user){
        finish();
        startActivity(getIntent());
    }
}
