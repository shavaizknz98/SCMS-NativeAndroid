package com.example.scms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.ViewfinderView;

import java.util.List;
import java.util.Random;

public class QRCodeScannerActivity extends AppCompatActivity implements DecoratedBarcodeView.TorchListener {

    //https://github.com/journeyapps/zxing-android-embedded/blob/master/sample/src/main/java/example/zxing/CustomScannerActivity.java

    private CaptureManager captureManager;
    private DecoratedBarcodeView scannerView;
    private Button toggleFlashBtn;
    private ViewfinderView viewfinderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_scanner);

        toggleFlashBtn = (Button) findViewById(R.id.toggleFlashBtn);
        scannerView = findViewById(R.id.zxing_barcode_scanner);
        viewfinderView = findViewById(R.id.zxing_viewfinder_view);

        scannerView.setTorchListener(this);

        scannerView.setTorchOff();
        toggleFlashBtn.setText("Flash: OFF");



        //remove flashlight if no flash
        if(!getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            toggleFlashBtn.setVisibility(View.GONE);
        }

        toggleFlashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toggleFlashBtn.getText().equals("Turn on flash") || toggleFlashBtn.getText().toString().toLowerCase().contains(":")) {
                    scannerView.setTorchOn();
                } else {
                    scannerView.setTorchOff();
                }
            }
        });

        captureManager = new CaptureManager(this, scannerView);
        captureManager.initializeFromIntent(getIntent(), savedInstanceState);
        captureManager.decode();


        changeMaskColor(null);
    }


    @Override
    protected void onResume() {
        super.onResume();
        captureManager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        captureManager.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        captureManager.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        captureManager.onSaveInstanceState(outState);
    }

    @Override
    public void onTorchOn() {
        toggleFlashBtn.setText("Turn off flash");
    }

    @Override
    public void onTorchOff() {
        toggleFlashBtn.setText("Turn on flash");
    }

    public void changeMaskColor(View view) {
        Random rnd = new Random();
        int color = Color.argb(100, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        viewfinderView.setMaskColor(color);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        captureManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
