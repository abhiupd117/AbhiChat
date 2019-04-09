package com.amandeep.abhichat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class ImageActivity extends AppCompatActivity {
    ImageView imageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_layout);
        imageView = findViewById(R.id.image);
        if (getIntent().getStringExtra("image_path")!=null) {
            Glide.with(this).load(getIntent().getStringExtra("image_path"))/*).apply(new RequestOptions().override(200, 100)).fitCenter()*/.into(imageView);
        }
    }
}
