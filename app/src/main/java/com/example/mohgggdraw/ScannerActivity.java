package com.example.mohgggdraw;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

/**
 * Activity for scanning QR codes.
 * Launches the scanner and handles the scanned results.
 */
public class ScannerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scanCode();
    }

    /**
     * Launches the QR code scanner with specified options.
     */
    private void scanCode() {
        scannerLauncher.launch(new ScanOptions()
                .setPrompt("Scan Event QR Code")
                .setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                .setCaptureActivity(CaptureAct.class));
    }

    /**
     * ActivityResultLauncher for handling scanner results.
     */
    private final ActivityResultLauncher<ScanOptions> scannerLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                Intent resultIntent = new Intent();
                if (result.getContents() == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_CANCELED, resultIntent);
                    finish();
                } else {
                    String qrText = result.getContents();
                    resultIntent.putExtra("qrTextKey", qrText);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
            }
    );
}