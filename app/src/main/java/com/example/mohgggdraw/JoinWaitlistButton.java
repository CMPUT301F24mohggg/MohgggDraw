package com.example.mohgggdraw;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


/***
 * dialogue popup warning for geolocation events
 * ***/
public class JoinWaitlistButton extends DialogFragment {
    Event event = new Event("olKgM5GAgkLRUqo97eVS","testname","testname","https://firebasestorage.googleapis.com/v0/b/mohgggdraw.appspot.com/o/event_images%2F1730963184849.jpg?alt=media&token=8c93f3c0-2e18-494a-95ec-a95b864ccdbd","testname","testname","testname","testname","testname",true);

    User user= new User();
    WaitlistFragment waitlistFragment;
    public JoinWaitlistButton(Event event, User user,WaitlistFragment waitlistFragment){
        super();
        this.event = event;
        this.user = user;
        this.waitlistFragment=waitlistFragment;

    }

    public JoinWaitlistButton(){
        super();

    }
    //dialogue for the geolocation event
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.event_geolocation_permission,null);
        //creating accept button that adds to database
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        Button button = view.findViewById(R.id.allow_access_button);
        button.setOnClickListener(v ->{
            new WaitinglistController(event).addUser(user);
            new UserDB().addEventToUserList(event.getEventId(),user.getUid());
            waitlistFragment.onDialogueFinished();
            dismiss();
        });
        return builder
                .setView(view)
                .setTitle("warning")
                .create();


    }

}
