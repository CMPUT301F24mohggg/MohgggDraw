package com.example.mohgggdraw;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class QrCreatedFragment extends Fragment {

    private Button qrShareButton;
    private ImageView newQrImageView;
    private EventQr eventQr;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String eventId = getArguments().getString("event_id");
        View view = inflater.inflate(R.layout.fragment_qrcode_created, container, false);
        newQrImageView = view.findViewById(R.id.new_qrcode_iv);
        qrShareButton = view.findViewById(R.id.qrshare_button);

        // Generate and hash QR
        EventQr eventQr = new EventQr(eventId);
        eventQr.generateQr();
        eventQr.hashQr();

        // Set bitmap to ImageView
        newQrImageView.setImageBitmap(eventQr.getQrBitmap());

        qrShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrcodeShare();
            }
        });
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

    private void qrcodeShare() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        Uri bmpUri;
        String textToShare = "Share event";
        bmpUri = saveImage(eventQr.getQrBitmap(), getActivity().getApplicationContext());
        share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        share.putExtra(Intent.EXTRA_STREAM, bmpUri);
        share.putExtra(Intent.EXTRA_SUBJECT, "Event QR Code");
        share.putExtra(Intent.EXTRA_TEXT, textToShare);
        startActivity(Intent.createChooser(share, "Share Event"));
    }
}
