package com.amandeep.abhichat;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public class MapViewActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {
    private LatLng currentLatLng;
    private Button btnSend;
    private Bitmap mapBitmap;
    private Uri mapURI;
    private MapView mMapView;
    // dimensions of map image
    private int mMapWidth = 600;
    private int mMapHeight = 800;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_view_activtity);
        btnSend = findViewById(R.id.btnSend);
        if (getIntent().getStringExtra("latitude") != null && getIntent().getStringExtra("longitude") != null) {
            currentLatLng = new LatLng(Double.parseDouble(getIntent().getStringExtra("latitude")), Double.parseDouble(getIntent().getStringExtra("longitude")));
        }
        if(getIntent().getStringExtra("type")!=null){
            if(getIntent().getStringExtra("type").equalsIgnoreCase("btn_click")){
                btnSend.setVisibility(View.VISIBLE);
            }else if(getIntent().getStringExtra("type").equalsIgnoreCase("chat_click")){
                btnSend.setVisibility(View.GONE);
            }
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        btnSend.setOnClickListener(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity
            //
            //
            // Compat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        //Place current location marker
      //  LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        googleMap.addMarker(markerOptions);

        //move map camera
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,11));
        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
                    Bitmap bitmap;

                    @Override
                    public void onSnapshotReady(Bitmap snapshot) {
                        bitmap = snapshot;
                        try {
                            FileOutputStream out = new FileOutputStream("/mnt/sdcard/Download/TeleSensors.png");
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                            Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                                    bitmap, 400, 400, false);
                            mapBitmap =resizedBitmap;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                googleMap.snapshot(callback);
            }
        });






    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSend:
                //ChatActivity.mapviewImagetoStorage(mapURI);
                if(mapBitmap!=null){
                    ChatActivity.savemapView(mapBitmap);
                    finish();
                }else{
                    Toast.makeText(this,"No Location found",Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

/*
    public Uri captureScreen(GoogleMap map)
    {
        final Bitmap[] mapBitmap = new Bitmap[1];
        final Uri[] mapURI = new Uri[1];
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback()
        {


            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                try {

                    getWindow().getDecorView().findViewById(android.R.id.content).setDrawingCacheEnabled(true);
                    Bitmap backBitmap = getWindow().getDecorView().findViewById(android.R.id.content).getDrawingCache();
                    Bitmap bmOverlay = Bitmap.createBitmap(
                            backBitmap.getWidth(), backBitmap.getHeight(),
                            backBitmap.getConfig());
                    Canvas canvas = new Canvas(bmOverlay);
                    canvas.drawBitmap(snapshot, new Matrix(), null);
                    canvas.drawBitmap(backBitmap, 0, 0, null);

                    OutputStream fout = null;


                    String filePath = System.currentTimeMillis() + ".jpeg";

                    try
                    {
                        fout = openFileOutput(filePath,
                                MODE_WORLD_READABLE);

                        // Write the string to the file
                        bmOverlay.compress(Bitmap.CompressFormat.JPEG, 90, fout);
                        fout.flush();
                        fout.close();

                        final ContentValues values = new ContentValues(2);
                        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                        values.put(MediaStore.Images.Media.DATA, filePath);
                        final Uri contentUriFile = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        mapURI[0] = contentUriFile;
                    }
                    catch (FileNotFoundException e)
                    {
                        // TODO Auto-generated catch block
                        Log.d("ImageCapture", "FileNotFoundException");
                        Log.d("ImageCapture", e.getMessage());
                        filePath = "";
                    }
                    catch (IOException e)
                    {
                        // TODO Auto-generated catch block
                        Log.d("ImageCapture", "IOException");
                        Log.d("ImageCapture", e.getMessage());
                        filePath = "";
                    }

                   // openShareImageDialog(filePath);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };




        map.snapshot(callback);



        return mapURI[0];
    }
*/

    public Uri captureScreen(GoogleMap googleMap)
    {
        final Uri[] mapURI = new Uri[1];
        final Bitmap[] bitmap = new Bitmap[1];
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback()
        {

            @Override
            public void onSnapshotReady(Bitmap snapshot)
            {
                // TODO Auto-generated method stub
                bitmap[0] = snapshot;

                OutputStream fout = null;

                String filePath = System.currentTimeMillis() + ".jpeg";

                try
                {
                    fout = openFileOutput(filePath,
                            MODE_WORLD_READABLE);

                    // Write the string to the file
                    bitmap[0].compress(Bitmap.CompressFormat.JPEG, 90, fout);
                    fout.flush();
                    fout.close();
                }
                catch (FileNotFoundException e)
                {
                    // TODO Auto-generated catch block
                    Log.d("ImageCapture", "FileNotFoundException");
                    Log.d("ImageCapture", e.getMessage());
                    filePath = "";
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    Log.d("ImageCapture", "IOException");
                    Log.d("ImageCapture", e.getMessage());
                    filePath = "";
                }

                final ContentValues values = new ContentValues(2);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                values.put(MediaStore.Images.Media.DATA, filePath);
                final Uri contentUriFile = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                mapURI[0] = contentUriFile;            }
        };

        googleMap.snapshot(callback);
        return mapURI[0];
    }
}
