package com.example.mohgggdraw;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.StrictMode;
import android.util.Log;
import android.view.View;

import androidx.core.content.FileProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mohgggdraw.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
//import com.google.zxing.MultiFormatWriter;

public class MainActivity extends AppCompatActivity {

    ImageView qrcodeIv;
    Button qrShareButton;
    Bitmap qrcodeBitmap;

    private AppBarConfiguration appBarConfiguration;

    private StorageReference storageReference;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storageReference = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        com.example.mohgggdraw.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.toolbar.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAnchorView(R.id.toolbar)
                .setAction("Action", null).show());

        // Adding the click listener for Edit Organizer button
        Button editOrganizerButton = findViewById(R.id.button_edit_organizer);
        editOrganizerButton.setOnClickListener(v -> {
            // Navigate to Organizer edit page
            Intent intent = new Intent(MainActivity.this, Organizer.class);
            startActivity(intent);
        });

        qrShareButton = findViewById(R.id.qrshare_button);
        qrcodeIv = findViewById(R.id.qrcode_iv);

        qrShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrcodeShare();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }


    private void qrcodeShare() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        Uri bmpUri;
        String textToShare = "Share event";
        bmpUri = saveImage(qrcodeBitmap, getApplicationContext());
        share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        share.putExtra(Intent.EXTRA_STREAM, bmpUri);
        share.putExtra(Intent.EXTRA_SUBJECT, "Event QR Code");
        share.putExtra(Intent.EXTRA_TEXT, textToShare);
        startActivity(Intent.createChooser(share, "Share Event"));
    }

    private static Uri saveImage(Bitmap image, Context context) {
        File imagesFolder = new File(context.getCacheDir(), "images");
        Uri uri = null;
        try {
            imagesFolder.mkdirs();
            File file = new File(imagesFolder, "event_qrcode.jpg");

            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(Objects.requireNonNull(context.getApplicationContext()),
                    "com.example.mohgggdraw" + ".provider", file);

        } catch (IOException e) {
            Log.d("TAG", "Exception" + e.getMessage());
        }
        return uri;
    }

    private void generateQr(String eventId) {
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix matrix = writer.encode(eventId, BarcodeFormat.QR_CODE, 600, 600);
            BarcodeEncoder encoder = new BarcodeEncoder();
            qrcodeBitmap = encoder.createBitmap(matrix);
            qrcodeIv.setImageBitmap(qrcodeBitmap);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

}
