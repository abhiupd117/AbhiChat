package com.amandeep.abhichat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.amandeep.abhichat.Adapter.MessageAdapter;
import com.amandeep.abhichat.AppUtils.Constant;
import com.amandeep.abhichat.Model.Chat;
import com.amandeep.abhichat.Model.Users;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("unchecked")


public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "";
    private static final int GALLERY = 1;
    CircleImageView profileImage;
    TextView username;
    //FirebaseUser fuser;
    static DatabaseReference reference;
    Intent intent;
    private FirebaseAuth firebaseAuth;
    public static Context context;
    ImageView add_btn;
    EditText message_write;
    Button btn_message_send;
    private FirebaseAuth mAuth;
    private String mCurrentUser;
    public Uri selectedImage;
    public Uri selectedVideo;
    TextView timeStamp;
    String mTimeStamp;
    StorageReference storageReference;
    private String imagemessgeUrl;
    ImageView gallary_image;
    public final int TAKE_PICTURE_CAMERA = 6;
    final int ACTIVITY_SELECT_IMAGE = 2;
    final int ACTIVITY_SELECT_VIDEO=3;
    ProgressBar progressBar;
    private static String mlatitude;
    private static String mLongitude;


    //ProgressDialog progressDialog;


    FirebaseStorage storage;

    static StorageReference storageRef;
    ImageView imageView;
    VideoView video_view_right;


    int MY_PERMISSIONS_REQUEST_LOCATION = 4;
    static String userId;
    MessageAdapter messageAdapter;
    private RecyclerView recyclerView;
    private static Users selectedUser;
    private boolean isFirstTime = false;
     String userPhotoStringLink;
    Bitmap bmframe;
    private String video_frame_url_for_uploade;
    private static String mapview_url_for_uploade;
    private static Uri mapView_Uri_from_drawable;
    private int REQUEST_CODE_CAPTURE_IMAGE=5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

         mapView_Uri_from_drawable = Uri.parse("android.resource://com.amandeep.abhichat/" + R.drawable.map_dummy);



        profileImage = findViewById(R.id.profile_img);
        username = findViewById(R.id.usernme_for_chat);
        context = ChatActivity.this;

        message_write = findViewById(R.id.message_box);
        add_btn = findViewById(R.id.btn_add_manymore);
        btn_message_send = findViewById(R.id.btn_message_sent);
        timeStamp=findViewById(R.id.time_stamp);

        recyclerView = findViewById(R.id.chat_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        // imageView =  findViewById(R.id.iv_show_gallary_image);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        intent = getIntent();
        //TODO this is selected user object. Need to play with this object
        selectedUser = (Users) intent.getSerializableExtra("selectedUser");
        //username = selectedUser.getName();
        username.setText(selectedUser.getName());//name of receiver
        userId = intent.getStringExtra("loggedUser");
        getPermission();




//        if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            // Permission is not granted
//        }



        // fuser=FirebaseAuth.getInstance().getCurrentUser();
        //Log.e("djhfuihfuis", String.valueOf(fuser));


        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(ChatActivity.this, "add on click", Toast.LENGTH_LONG).show();
                final Dialog dialog = new Dialog(ChatActivity.this);
                dialog.setContentView(R.layout.media_cardview);
                dialog.setTitle("Title...");
                ImageView go_to_gallary = dialog.findViewById(R.id.btn_go_to_gallary);

                ImageView go_for_camera = dialog.findViewById(R.id.btn_open_camera);

                ImageView go_for_current_location = dialog.findViewById(R.id.btn_hold_current_loation);


                go_to_gallary.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        choosePhotoFromGallary();
                        dialog.dismiss();
                    }
                });
                go_for_current_location.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCurrentPositionUsingGPS();
                        dialog.dismiss();



                    }
                });
                go_for_camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        takePhotoFromCamera();
                        dialog.dismiss();
                    }
                });
                dialog.show();


            }
        });
        reference = FirebaseDatabase.getInstance().getReference("User").child(userId);

        btn_message_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (message_write != null) {
                    //TODO send text
                    //TODO set image variable to null
                    String msg = message_write.getText().toString();
                    if (!msg.equals("")) {
//                    Toast.makeText(context,"Receiver = "+fuser.getUid()+" = name = "+fuser.getDisplayName(),Toast.LENGTH_SHORT).show();
                        sendMessage(userId, selectedUser.getId(), msg);
                        Log.e("UserID", userId);
                        message_write.setText("");
                    } else {
                        Toast.makeText(context, "Sorry, Can't sent empty emssage", Toast.LENGTH_LONG).show();

                    }

                } else {
                    //TODO send image
                    //TODO set message text to null
                    //take another variable for image and send
                   /* String Imagemsg=imagemessgeUrl;
                    sendMessage(userId,selectedUser.getId(),Imagemsg);*/
                }

            }
        });

        new readinguserfromDB().execute();

    }

    private void sendMessage(String sender, String receiver, String message) {
        reference = FirebaseDatabase.getInstance().getReference();
        Log.e("send Chat", "called");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("timestamp",getTimeStamp());
        reference.child("messages").push().setValue(hashMap);

    }
    private static String getTimeStamp(){
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        return ts;
    }





    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == Activity.RESULT_OK && data != null) {
                if (requestCode == ACTIVITY_SELECT_IMAGE)
                {
                    Bundle extras = data.getExtras();

                    selectedImage = data.getData();


                    final StorageReference sRef = storageRef.child(Constant.IMAGES_MESSAGES + System.currentTimeMillis() + "." + getFileExtension(selectedImage));
                    UploadTask uploadTask = sRef.putFile(selectedImage);
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
                    {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {

                                throw task.getException();
                            }
                            return sRef.getDownloadUrl();

                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            if (task.isSuccessful()) {
                                Uri downlodeUri = task.getResult();
                                System.out.println("Uploaded" + downlodeUri);
                                if (downlodeUri != null) {
                                    userPhotoStringLink = downlodeUri.toString();
                                    Log.e(" image url", userPhotoStringLink);
                                    reference = FirebaseDatabase.getInstance().getReference();
                                    Log.e("send image Chat", "called");
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("sender", userId);
                                    hashMap.put("receiver", selectedUser.getId());
                                    hashMap.put("imageUrl", userPhotoStringLink);
                                    hashMap.put("timestamp",getTimeStamp());
                                    reference.child("messages").push().setValue(hashMap);


                                }
                            } else {
                                Log.d(TAG, "Something wrong with user photo String link which is comming from DB");
                            }
                        }
                    });
                }
                if (requestCode == ACTIVITY_SELECT_VIDEO) {
                    selectedVideo = data.getData();
                    if (selectedVideo != null) {


                        //TODO GET AND UPLOADE VIDEO FRAME TO FIREBASE DATABASE

                        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                        mediaMetadataRetriever.setDataSource(ChatActivity.this, selectedVideo);
                        bmframe = mediaMetadataRetriever.getFrameAtTime(1000);
                        final Uri image_video_Frame = getImageUri(getBaseContext(), bmframe);

                        //  store & retrieve this string to firebase
                        //TODO SENDING VIDEO SNAPE IMAGE TO FIREBASE STORAGE
                        final StorageReference sframeRef = storageRef.child(Constant.IMAGES_MESSAGES + System.currentTimeMillis() + "." + getFileExtension(image_video_Frame));
                        UploadTask uploadTask_video_frame = sframeRef.putFile(image_video_Frame);
                        uploadTask_video_frame.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                return sframeRef.getDownloadUrl();
                            }

                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri video_frame_url = task.getResult();
                                    System.out.println("uploaded" + video_frame_url);
                                    if (video_frame_url != null) {
                                        video_frame_url_for_uploade = video_frame_url.toString();
                                    }
                                }

                            }
                        });
                    }


                    //TODO UPLOADE VIDEO TO FIREBASE DATABSE

                    final StorageReference svideoRef = storageRef.child(Constant.VIDEO_MESSAGES + System.currentTimeMillis() + "." + getFileExtension(selectedVideo));
                    /*progressDialog.setMessage("Sending Video...");
                    progressDialog.setMessage("Sending...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();*/

                    UploadTask uploadTask1 = svideoRef.putFile(selectedVideo);
                    uploadTask1.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {

                                throw task.getException();
                            }
                            return svideoRef.getDownloadUrl();

                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            if (task.isSuccessful()) {
                                Uri downlode_video_Uri = task.getResult();
                                System.out.println("Uploaded" + downlode_video_Uri);
                                if (downlode_video_Uri != null) {
                                    // progressBar.setMax(100);

                                    String user_video_StringLink = downlode_video_Uri.toString();
                                    Log.e("chat video url", user_video_StringLink);
                                    reference = FirebaseDatabase.getInstance().getReference();
                                    Log.e("send video Chat", "called");
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("sender", userId);
                                    hashMap.put("receiver", selectedUser.getId());
                                    hashMap.put("videoUrl", user_video_StringLink);
                                    hashMap.put("timestamp", getTimeStamp());
                                    hashMap.put("imageUrl", video_frame_url_for_uploade);
                                    reference.child("messages").push().setValue(hashMap);
                                    //  progressDialog.hide();


                                }
                            } else {
                                Log.d(TAG, "Something wrong with user video String link which is comming from DB");
                            }
                        }
                    });
                }

                if (requestCode==TAKE_PICTURE_CAMERA)
                {

                        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                        selectedImage=getImageUri(context,thumbnail);


                        if (selectedImage!=null) {

                            final StorageReference sRef = storageRef.child(Constant.IMAGES_MESSAGES + System.currentTimeMillis() + "." + getFileExtension(selectedImage));
                            UploadTask uploadTask = sRef.putFile(selectedImage);
                            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {

                                        throw task.getException();
                                    }
                                    return sRef.getDownloadUrl();

                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {

                                    if (task.isSuccessful()) {
                                        Uri downlodeUri = task.getResult();
                                        System.out.println("Uploaded" + downlodeUri);
                                        if (downlodeUri != null) {
                                            userPhotoStringLink = downlodeUri.toString();
                                            Log.e(" image url", userPhotoStringLink);
                                            reference = FirebaseDatabase.getInstance().getReference();
                                            Log.e("send image Chat", "called");
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("sender", userId);
                                            hashMap.put("receiver", selectedUser.getId());
                                            hashMap.put("imageUrl", userPhotoStringLink);
                                            hashMap.put("timestamp", getTimeStamp());
                                            reference.child("messages").push().setValue(hashMap);


                                        }
                                    } else {
                                        Log.d(TAG, "Something wrong with user photo String link which is comming from DB");
                                    }
                                }
                            });
                        }

                    }



            }





            }




    private static Uri getImageUri(Context context, Bitmap inImage) {
        Uri uri = null;
        if(inImage != null){
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            try {
                bytes.flush();
                bytes.close();
                String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
                uri = Uri.parse(path);
            } catch (Exception e) {
                Log.e("Exception : ",e.getMessage());
                e.printStackTrace();
            }


        }else{
            Toast.makeText(context,"Bitmap is null",Toast.LENGTH_LONG).show();
        }

        return uri;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private void readMessage(final String myId, final String userId, final String imageurl) {

        reference = FirebaseDatabase.getInstance().getReference().child("messages");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                final List<Chat> mChat = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // snapshot.

                    //Chat chat = snapshot.getValue(Chat.class);
                    Chat chat= new Chat();
                    final Object value = snapshot.getValue();


                    if(value instanceof HashMap){  //TO SET DATA MANUALLY INTO CHAT MODEL
                      //  Log.e("Map","Map");
                        Map<String , Object> valuesInMap = (Map<String, Object>) value;
                        if(valuesInMap.containsKey("imageUrl")){
                            String imageUrl = (String) valuesInMap.get("imageUrl");
                            chat.setMessage_img(imageUrl);

                        }
                        chat.setSender((String) valuesInMap.get("sender"));
                        chat.setReceiver((String) valuesInMap.get("receiver"));
                        if (valuesInMap.containsKey("message"))
                        {
                            chat.setMessage((String) valuesInMap.get("message"));
                        }


                        if(valuesInMap.containsKey("videoUrl")){
                            String videoUrl = (String) valuesInMap.get("videoUrl");
                            chat.setVideoUrl(videoUrl);
                        }


                        if (valuesInMap.containsKey("timestamp"));
                        {
                            String timestamp = (String) valuesInMap.get("timestamp");
                            chat.setTimestamp(timestamp);

                        }
                        if (valuesInMap.containsKey("lat")){
                            String lat= (String) valuesInMap.get("lat");
                            chat.setLat(lat);
                        }
                        if (valuesInMap.containsKey("longitude")){
                            String longitude= (String) valuesInMap.get("longitude");
                            chat.setLongitude(longitude);
                        }



                    }
                    if (chat.getReceiver() != null && chat.getReceiver() != null) {
                        if (!chat.getReceiver().isEmpty() && !chat.getReceiver().isEmpty()) {
                            if (chat.getReceiver().equals(myId) && chat.getSender().equals(userId) || chat.getReceiver().equals(userId) && chat.getSender().equals(myId)) {
                                Log.e("Add Chat", "called");

                                mChat.add(chat);

                            }
                        }

                    }
                    messageAdapter = new MessageAdapter(ChatActivity.this, mChat, imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static String getFileExtension(Uri uri) {
        if (uri != null) {
            ContentResolver cR = context.getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            return mime.getExtensionFromMimeType(cR.getType(uri));
        } else {

            return null;
        }
    }

private void getPermission(){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                //do your check here
                if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG,"Permission is granted");
                } else {
                    Log.v(TAG,"Permission is revoked");
                    ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }

                if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "Permission not granted");
                    ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                    //File write logic here
                }
                else {
                    //Toast.makeText(context, "READ permission alread", Toast.LENGTH_LONG).show();
                }
                if (ContextCompat.checkSelfPermission(ChatActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)
                           ChatActivity.this, Manifest.permission.CAMERA)) {


                    } else {
                        ActivityCompat.requestPermissions((Activity) ChatActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                6);
                    }

                }


                if (ContextCompat.checkSelfPermission(ChatActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(ChatActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ChatActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission  .ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant

                    return;
                }


            }
            else {
                    //permission is automatically granted on sdk<23 upon installation
                Log.v(TAG,"Permission is granted");


        }
        } catch (Exception e){
            e.printStackTrace();
        }
}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
         if(requestCode==MY_PERMISSIONS_REQUEST_LOCATION){
             if(grantResults[0]==PackageManager.PERMISSION_GRANTED){

             }else{
                 Toast.makeText(context,"Permission denied",Toast.LENGTH_SHORT).show();
             }
             return;
         }
    }

    public void choosePhotoFromGallary () {

        final CharSequence[] options = {"Images", "Videos", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setTitle("Select From...");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Images")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                   // intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(intent, ACTIVITY_SELECT_IMAGE);
                } else if (options[item].equals("Videos")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                  //  intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(intent, ACTIVITY_SELECT_VIDEO);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
                dialog.dismiss();
            }
        });
        builder.show();
    }
    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_PICTURE_CAMERA);
    }
    public void cropImagefromGallary(){
    }

    //TODO GETTING CURRENT POSITION USING LATITUDE AND LONGITUDE
    private void mCurrentPositionUsingGPS(){


        final GPSTracker gpsTracker= new GPSTracker(this);
        if (gpsTracker.isGPSTrackingEnabled){

            mlatitude=String.valueOf(gpsTracker.latitude);

            mLongitude=String.valueOf(gpsTracker.longitude);

            /*String map_URL_Image_view=latlongtoGoogleMapApi(mlatitude,mLongitude);
            getMapimage_file(map_URL_Image_view);*/
           // mapviewImagetoStorage();
            Intent intent = new Intent(this,MapViewActivity.class);
            intent.putExtra("latitude",mlatitude);
            intent.putExtra("longitude",mLongitude);
            intent.putExtra("type","btn_click");

            startActivity(intent);
        }

    }

    private String latlongtoGoogleMapApi(String setlat, String setLong){
        String google_map_viewApi="https://maps.googleapis.com/maps/api/staticmap?center= " + setlat+ "," + setLong + "&zoom=13&siz" +
                "e=600x300&maptype=roadmap&markers=color:blue%7Clabel:S%7C" +setlat + "," +setLong +"&key=AIzaSyANXJoo1l8XgxkvnBACcMY4entGGnFT8Q4";

        return google_map_viewApi;
    }


    /*private  void getMapimage_file(String path){
        Glide.with(this)
                .asBitmap()
                .load(path)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                       *//* ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        resource.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

//you can create a new file name "test.jpg" in sdcard folder.
                        File f = new File(Environment.getExternalStorageDirectory()
                                + File.separator + "test.jpg");
                        try {
                            f.createNewFile();
                            FileOutputStream fo = null;
                            fo = new FileOutputStream(f);
                            fo.write(bytes.toByteArray());
                            fo.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }*//*
                        //savemapView(resource);
                    }

                });
    }*/
    public static void savemapView(final Bitmap bitmap){


        // TODO SENDING MAPVIEW IMAGE TO FIREBASE STORAGE

        final StorageReference sframeRef = storageRef.child(Constant.IMAGES_MESSAGES + System.currentTimeMillis() + "." + getFileExtension(getImageUri(context,bitmap)));
        UploadTask uploadTask_mapview = sframeRef.putFile(getImageUri(context,bitmap));
        uploadTask_mapview.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return sframeRef.getDownloadUrl();
            }

        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri map_view_Url = task.getResult();
                    System.out.println("uploaded" + map_view_Url);
                    if (map_view_Url != null) {
                        mapview_url_for_uploade = map_view_Url.toString();
                        reference = FirebaseDatabase.getInstance().getReference();
                        Log.e("send video Chat", "called");
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("sender", userId);
                        hashMap.put("receiver", selectedUser.getId());
                        hashMap.put("imageUrl", mapview_url_for_uploade);
                        hashMap.put("timestamp",getTimeStamp());
                        hashMap.put("lat", mlatitude);
                        hashMap.put("longitude",mLongitude);
                        reference.child("messages").push().setValue(hashMap);                    }
                }
                Toast.makeText(context,"Your current location sent",Toast.LENGTH_LONG).show();

            }
        });
    }

    //TODO DRAWABLE RESOURSE TO URI



    public static void mapviewImagetoStorage(Uri uri)
    {
        final StorageReference sframeRef = storageRef.child(Constant.IMAGES_MESSAGES + System.currentTimeMillis() + "." + getFileExtension(uri/*mapView_Uri_from_drawable*/));
        UploadTask uploadTask_mapview = sframeRef.putFile(uri/*mapView_Uri_from_drawable*/);
        uploadTask_mapview.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return sframeRef.getDownloadUrl();
            }

        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri map_view_Url = task.getResult();
                    System.out.println("uploaded" + map_view_Url);
                    if (map_view_Url != null) {
                        mapview_url_for_uploade = map_view_Url.toString();
                        reference = FirebaseDatabase.getInstance().getReference();
                        Log.e("send video Chat", "called");
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("sender", userId);
                        hashMap.put("receiver", selectedUser.getId());
                        hashMap.put("imageUrl", mapview_url_for_uploade);
                        hashMap.put("timestamp", getTimeStamp());
                        hashMap.put("lat", mlatitude);
                        hashMap.put("longitude", mLongitude);
                        reference.child("messages").push().setValue(hashMap);
                        Toast.makeText(context,"Your current location sent",Toast.LENGTH_LONG).show();
                    }

                }
            }
        });
    }

    private class readinguserfromDB extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog= new ProgressDialog(ChatActivity.this);

        @Override
        protected String doInBackground(String... params) {


            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Users users = dataSnapshot.getValue(Users.class);
                    //username.setText(users.getName());

//                Glide.with(context).load(users.getImage_url()).into(profileImage);

                    readMessage(userId, selectedUser.getId(), users.getImageurl());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
          //  progressDialog.dismiss();
          //  progressDialog = null;

        }

        @Override
        protected void onPreExecute() {

          //  progressDialog.setMessage("loading...");
           // progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}




