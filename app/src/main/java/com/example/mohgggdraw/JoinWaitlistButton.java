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
    Event event;
    User user;
    public JoinWaitlistButton(Event event, User user){
        super();
        this.event = event;
        this.user = user;

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.join_activity,null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        Button button = view.findViewById(R.id.accept_terms_button);
        button.setOnClickListener(v ->{
            new WaitinglistController(user, event).addUser(user);
        });
        return builder
                .setView(view)
                .setTitle("warningskull")
                .create();


    }
}
