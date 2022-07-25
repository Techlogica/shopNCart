package com.tids.shopncart.product;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.tids.shopncart.R;
import com.tids.shopncart.utils.BaseActivity;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import es.dmoral.toasty.Toasty;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class EditProductScannerViewActivity extends BaseActivity implements ZXingScannerView.ResultHandler {


    int currentApiVersion = Build.VERSION.SDK_INT;
    private ZXingScannerView scannerView;
    ImageView backBtn;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product_scanner_view);
//        getSupportActionBar().setHomeButtonEnabled(true); //for back button
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
//        getSupportActionBar().setTitle(R.string.qr_barcode_scanner);
        toolbar = findViewById(R.id.toolbar);
        backBtn = findViewById(R.id.menu_back);
        setSupportActionBar(toolbar);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (currentApiVersion >= Build.VERSION_CODES.M) {
            requestCameraPermission();
        }

        scannerView = new ZXingScannerView(EditProductScannerViewActivity.this);
        setContentView(scannerView);
        scannerView.startCamera();
        scannerView.setResultHandler(EditProductScannerViewActivity.this);


    }


    @Override
    public void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }


    @Override
    public void handleResult(Result result) {
        final String myResult = result.getText();

        //set result in main activity or previous activity
        EditProductActivity.etxtProductCode.setText(myResult);
        Log.d("QRCodeScanner", result.getText());
        Log.d("QRCodeScanner", result.getBarcodeFormat().toString());

        onBackPressed();


    }


    //Runtime permission
    private void requestCameraPermission() {

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        scannerView = new ZXingScannerView(EditProductScannerViewActivity.this);
                        setContentView(scannerView);
                        scannerView.startCamera();
                        scannerView.setResultHandler(EditProductScannerViewActivity.this);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            Toasty.info(EditProductScannerViewActivity.this, R.string.camera_permission, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                        token.continuePermissionRequest();
                    }
                }).check();
    }


//    //for back button
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            this.finish();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}
