package com.example.mohgggdraw;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WaitlistFragment extends AppCompatActivity {
    Event event;
    User user;
    String path;
    Bitmap bmp;
    ImageView iv;

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

        Intent intent = getIntent();
        event = (Event) intent.getExtras().getSerializable("event");
        user = (User) intent.getExtras().getSerializable("user");
        TextView name = (TextView) findViewById(R.id.textView);
        name.setText(event.getName());
        path = event.getPath();
        iv = (ImageView) findViewById(R.id.eventimage);
        new imageconvert().execute();
        iv.setColorFilter(R.drawable.ic_launcher_background);



        Button joinButton = findViewById(R.id.join_button);
        joinButton.setOnClickListener(v -> {
            new JoinWaitlistButton(event, user).show(getSupportFragmentManager(), "join");
        });


    }

    public void enroll(Event event) {

    }

    public class imageconvert extends AsyncTask {

        @Override
        protected Bitmap doInBackground(Object[] objects) {
            InputStream in = null;
            System.out.println("IM HERERREEE");
            int responseCode = -1;
            try {

                URL url = new URL(path);//"http://192.xx.xx.xx/mypath/img1.jpg
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setDoInput(true);
                con.connect();
                responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    //download
                    in = con.getInputStream();
                    bmp = BitmapFactory.decodeStream(in);
                    in.close();

                }

            } catch (Exception ex) {
                Log.e("Exception", ex.toString());
            }
            return bmp;
        }

        protected void onPostExcecute(Void aVoid) {
            super.onPostExecute(aVoid);

            iv.setImageBitmap(bmp);

        }


    }
}
