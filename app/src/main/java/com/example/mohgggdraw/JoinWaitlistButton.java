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

public class JoinWaitlistButton extends DialogFragment {
    Event event = new Event();
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

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.join_activity,null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        Button button = view.findViewById(R.id.accept_terms_button);
        button.setOnClickListener(v ->{
            new WaitinglistController(user, event).addUser(user);
            waitlistFragment.onDialogueFinished();
            dismiss();
        });
        return builder
                .setView(view)
                .setTitle("warningskull")
                .create();


    }

}
