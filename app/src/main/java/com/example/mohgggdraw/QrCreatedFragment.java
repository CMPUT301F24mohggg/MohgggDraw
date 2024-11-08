package com.example.mohgggdraw;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.content.ContextWrapper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class QrCreatedFragment extends Fragment {
    Context context;
    private Button qrShareButton;
    private Button viewEventButton;
    private ImageView newQrImageView;
    private EventQr eventQr;

    public QrCreatedFragment(EventQr eventQr) {
        this.eventQr = eventQr;
        this.context = getActivity();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qrcode_created, container, false);
        newQrImageView = view.findViewById(R.id.new_qrcode_iv);
        qrShareButton = view.findViewById(R.id.qrshare_button);

        // Set bitmap to ImageView
        newQrImageView.setImageBitmap(eventQr.getQrBitmap());


//        viewEventButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                DocToEvent docToEvent = new DocToEvent(eventQr.getEventId());
//
//                Event myevent = docToEvent.createEvent();
//                Fragment fragment = new WaitlistFragment(myevent);
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.fragment_container, fragment);
//                fragmentTransaction.addToBackStack(null);
//
//                fragmentTransaction.show(fragment).commit();
//            }
//        });


        qrShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrShare(eventQr.getQrBitmap());
            }
        });
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    private void qrShare(Bitmap qrBitmap) {

        String stringPath = MediaStore.Images.Media.insertImage(this.getActivity().getContentResolver(),
                qrBitmap, "Event QR Code", null);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(stringPath));
        intent.putExtra(Intent.EXTRA_TEXT, "Share this event!");

        startActivity(Intent.createChooser(intent, "Share Event QR Code"));


    }
}
