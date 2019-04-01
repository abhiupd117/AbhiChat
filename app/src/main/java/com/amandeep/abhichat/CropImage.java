package com.amandeep.abhichat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.support.v7.widget.Toolbar;

import java.io.File;

public class CropImage extends AppCompatActivity {
    ImageView imageView1;
    Toolbar toolbar1;
    File file;
    Uri uri;
    Intent gallaryIntent, cameraInetnt, cropIntent;

    final  int RequestPermissioncode=1;
    DisplayMetrics displayMetrics;
    int width, hight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);
        toolbar1=findViewById(R.id.toolbar);
        toolbar1.setTitle("Crop image");
        setSupportActionBar(toolbar1);
        imageView1=findViewById(R.id.crop_imageview);
        int permisioncheck= ContextCompat.checkSelfPermission(CropImage.this, Manifest.permission.CAMERA);
        if (permisioncheck== PackageManager.PERMISSION_DENIED)
        {
            RequestRunTimePermission();
        }
    }

    private void RequestRunTimePermission() {
    if (ActivityCompat.shouldShowRequestPermissionRationale(CropImage.this, Manifest.permission.CAMERA));
    else {
        ActivityCompat.requestPermissions(CropImage.this,new String[]{Manifest.permission.CAMERA},RequestPermissioncode);
    }
    }
}
