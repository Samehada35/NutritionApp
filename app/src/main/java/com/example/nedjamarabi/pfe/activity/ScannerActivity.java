package com.example.nedjamarabi.pfe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.nedjamarabi.pfe.R;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.List;

import info.androidhive.barcode.BarcodeReader;

public class ScannerActivity extends AppCompatActivity implements BarcodeReader.BarcodeReaderListener {
    
    private BarcodeReader barcodeReader;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        barcodeReader = (BarcodeReader) getSupportFragmentManager().findFragmentById(R.id.barcode_fragment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    
    @Override
    public void onScanned(Barcode barcode) {
        barcodeReader.playBeep();
        if (barcode.displayValue.matches("\\d+")) {
            Intent intent = new Intent(ScannerActivity.this, RechercheAliment.class);
            intent.putExtra("code", barcode.displayValue);
            intent.putExtra("repas", getIntent().getIntExtra("repas", 0));
            intent.putExtra("date", getIntent().getStringExtra("date"));
            startActivity(intent);
            finish();
            
        }
        barcodeReader.resumeScanning();
    }
    
    @Override
    public void onScannedMultiple(List<Barcode> list) {
    }
    
    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {
    }
    
    @Override
    public void onCameraPermissionDenied() {
        finish();
    }
    
    @Override
    public void onScanError(String s) {
        Toast.makeText(getApplicationContext(), "Error occurred while scanning " + s, Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}