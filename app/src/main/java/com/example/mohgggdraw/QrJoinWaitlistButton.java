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
public class QrJoinWaitlistButton extends DialogFragment {
    Event event = new Event("olKgM5GAgkLRUqo97eVS","testname","testname","https://firebasestorage.googleapis.com/v0/b/mohgggdraw.appspot.com/o/event_images%2F1730963184849.jpg?alt=media&token=8c93f3c0-2e18-494a-95ec-a95b864ccdbd","testname","testname","testname","testname","testname",true);

    User user= new User();
    QrWaitlistFragment qrWaitlistFragment;
    public QrJoinWaitlistButton(Event event, User user, QrWaitlistFragment qrWaitlistFragment){
        super();
        this.event = event;
        this.user = user;
        this.qrWaitlistFragment = qrWaitlistFragment;

    }

    public QrJoinWaitlistButton(){
        super();

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.join_event,null);
        //creating accept button that adds to database
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        Button button = view.findViewById(R.id.accept_terms_button);
        button.setOnClickListener(v ->{
            new WaitinglistController(event).addUser(user);
            qrWaitlistFragment.onDialogueFinished();
            dismiss();
        });
        return builder
                .setView(view)
                .setTitle("warning")
                .create();


    }

}
