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
    private Bitmap qrBitmap;
    private int qrHash;

    /**
     * Constructs an EventQr object with the specified event ID.
     *
     * @param eventId the unique identifier for the event
     */
    public EventQr(String eventId) {
        this.eventId = eventId;
        this.qrBitmap = null;
        this.qrHash = 0;
    }

    /**
     * Generates a QR code for the event ID and stores it as a bitmap.
     */
    public void generateQr() {
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix matrix = writer.encode(eventId, BarcodeFormat.QR_CODE, 600, 600);
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
    public void hashQr() {
        if (this.qrBitmap == null) {
            generateQr();
        }
        try {
            int hashedQrcode = 37 * this.qrBitmap.hashCode();
            this.setQrHash(hashedQrcode);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing QR code", e);
        }
    }

    // Getters and Setters

    /**
     * @return the event ID associated with this QR code
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Updates the event ID.
     *
     * @param eventId the new event ID
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * @return the QR code bitmap
     */
    public Bitmap getQrBitmap() {
        return qrBitmap;
    }

    /**
     * Updates the QR code bitmap.
     *
     * @param qrBitmap the new QR code bitmap
     */
    private void setQrBitmap(Bitmap qrBitmap) {
        this.qrBitmap = qrBitmap;
    }

    /**
     * @return the hash of the QR code bitmap
     */
    public int getQrHash() {
        return qrHash;
    }

    /**
     * Updates the QR code hash value.
     *
     * @param qrHash the new QR code hash
     */
    private void setQrHash(int qrHash) {
        this.qrHash = qrHash;
    }
}