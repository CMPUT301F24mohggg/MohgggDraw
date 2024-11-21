package com.example.mohgggdraw;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AlertDialogLayout;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;

public class ScannerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scanCode();
    }


    private void scanCode() {
        scannerLauncher.launch(new ScanOptions()
                .setPrompt("Scan Event QR Code")
                .setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                .setCaptureActivity(CaptureAct.class));
    }



    private ActivityResultLauncher<ScanOptions> scannerLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                Intent resultIntent = new Intent();
                if (result.getContents() == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_CANCELED, resultIntent);
                    finish();
                }
                else {
                    String qrText = result.getContents();
                    resultIntent.putExtra("qrTextKey", qrText);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
            }
    );
}
