package com.example.mohgggdraw;

import android.graphics.Bitmap;
import android.media.Image;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class EventQr {
    private String eventId;
    private Bitmap qrBitmap;
    private int qrHash;


    public EventQr(String eventId) {
        this.eventId = eventId;
        this.qrBitmap = null;
        this.qrHash = 0;
    }


    public void generateQr() {
        // Generate QR and sends QR code to fragment ImageView
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix matrix = writer.encode(eventId, BarcodeFormat.QR_CODE, 600, 600);
            BarcodeEncoder encoder = new BarcodeEncoder();
            qrBitmap = encoder.createBitmap(matrix);
            this.setQrBitmap(qrBitmap);

        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

    public void hashQr() {
        // Generate and hash QR if does not exist
        if (this.qrBitmap == null) {
            generateQr();
        }
        try {
            int hashedQrcode;
            hashedQrcode = 37 * this.qrBitmap.hashCode();
            this.setQrHash(hashedQrcode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Bitmap getQrBitmap() {
        return qrBitmap;
    }

    private void setQrBitmap(Bitmap qrBitmap) {
        this.qrBitmap = qrBitmap;
    }

    public int getQrHash() {
        return qrHash;
    }

    private void setQrHash(int qrHash) {
        this.qrHash = qrHash;
    }
}
