package com.example.mohgggdraw;

import android.graphics.Bitmap;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import java.io.Serializable;

/**
 * Represents an event QR code.
 * This class manages QR code generation and hashing for an event based on its ID.
 */
public class EventQr implements Serializable {

    private String eventId;
    private String eventTitle;
    private Bitmap qrBitmap;
    private String qrHash;

    /**
     * Constructs an EventQr object with the specified event ID.
     *
     * @param eventId the unique identifier for the event
     */
    public EventQr(String eventId, String eventTitle) {
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.qrBitmap = null;
        this.qrHash = generateHash();
    }

    /**
     * Generates a QR code for the event ID and stores it as a bitmap.
     */
    public void generateQr() {
        MultiFormatWriter writer = new MultiFormatWriter();

        try {
            BitMatrix matrix = writer.encode(this.qrHash, BarcodeFormat.QR_CODE, 600, 600);
            BarcodeEncoder encoder = new BarcodeEncoder();
            qrBitmap = encoder.createBitmap(matrix);
            this.setQrBitmap(qrBitmap);
        } catch (WriterException e) {
            throw new RuntimeException("Error generating QR code", e);
        }
    }

    /**
     * Hashes the QR code bitmap and stores the hash value.
     * If the QR code bitmap doesn't exist, it will be generated first.
     */
    private String generateHash() {
        int hash = 37 * (this.eventId.hashCode() + this.eventTitle.hashCode());
        return String.valueOf(hash); // Ensure the hash is positive
    }


    // Getters and Setters
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

    public String getQrHash() {
        return qrHash;
    }

    private void setQrHash(String qrHash) {
        this.qrHash = qrHash;
    }
}