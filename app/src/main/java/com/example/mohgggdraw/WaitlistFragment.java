package com.example.mohgggdraw;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import org.w3c.dom.Text;

public class WaitlistFragment extends AppCompatActivity {
    Event event;
    User user;
    public WaitlistFragment() {
        super();


    }



    /*
    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_event, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        Button confirm = view.findViewById(R.id.join_button);

        return builder
                .setView(view)
                .setTitle("")
                .setPositiveButton("Allow Access", (dialog, which)->{
                  enroll(event);
                })
                .create();
}
*/
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.view_event);

        Intent intent  = getIntent();
        event = (Event)intent.getExtras().getSerializable("event");
        user = (User)intent.getExtras().getSerializable("user");
        TextView name = (TextView) findViewById(R.id.textView);
        name.setText(event.getName());



        Button joinButton = findViewById(R.id.join_button);
        joinButton.setOnClickListener(v-> {
            new JoinWaitlistButton(event, user).show(getSupportFragmentManager(),"join");
        });




    }
        public void enroll(Event event) {

    }


}
