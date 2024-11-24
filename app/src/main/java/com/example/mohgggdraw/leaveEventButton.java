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
 * warning if leaving the waitlist
 *
 * ***/
public class leaveEventButton extends DialogFragment {
    Event event;
    User user;
    WaitlistFragment waitlistFragment;
    public leaveEventButton(Event event, User user, WaitlistFragment waitlistFragment){
        super();
        this.event = event;
        this.user = user;
        this.waitlistFragment = waitlistFragment;

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.leave_event,null);
        //creating leave button taht removes from database
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        Button button = view.findViewById(R.id.leaveButton);
        button.setOnClickListener(v ->{
            new WaitinglistController(event).removeUser(user);
            waitlistFragment.onDialogueFinished();
            dismiss();
        });
        return builder
                .setView(view)
                .setTitle("Warning: you are leaving the event")
                .create();


    }
}

