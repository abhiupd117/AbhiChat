package com.amandeep.abhichat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
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
import java.io.IOException;
import java.io.InputStream;
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
    DatabaseReference reference;
    Intent intent;
    private FirebaseAuth firebaseAuth;
    Context context;
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
    public final int TAKE_PICTURE = 1;
    final int ACTIVITY_SELECT_IMAGE = 2;
    final int ACTIVITY_SELECT_VIDEO=3;
    ProgressBar progressBar;
    //ProgressDialog progressDialog;


    FirebaseStorage storage;

    StorageReference storageRef;
    ImageView imageView;
    VideoView video_view_right;



    String userId;
    MessageAdapter messageAdapter;
    private RecyclerView recyclerView;
    private Users selectedUser;
    private boolean isFirstTime = false;
     String userPhotoStringLink;
    Bitmap bmframe;
    private String video_frame_url_for_uploade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        profileImage = findViewById(R.id.profile_img);
        username = findViewById(R.id.usernme_for_chat);

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

        final GPSTracker gpsTracker= new GPSTracker(this);


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
    private String getTimeStamp(){
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        return ts;
    }





    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == Activity.RESULT_OK && data != null) {
                if (requestCode == ACTIVITY_SELECT_IMAGE) {
                    Bundle extras = data.getExtras();

                    selectedImage = data.getData();


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
                                    hashMap.put("timestamp",getTimeStamp());
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
            }





            }




    private Uri getImageUri(Context context, Bitmap inImage) {
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

    public String getFileExtension (Uri uri) {
        if (uri != null) {
            ContentResolver cR = getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            return mime.getExtensionFromMimeType(cR.getType(uri));
        } else {

            return null;
        }
    }

private void getPermission(){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //do your check here
                if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG,"Permission is granted");
                } else {
                    Log.v(TAG,"Permission is revoked");
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }

                if (ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Log.v(TAG, "Permission not granted");
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                    //File write logic here
                }
                else {
                    Toast.makeText(context, "READ permission alread", Toast.LENGTH_LONG).show();
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
    public void cropImagefromGallary(){
    }

    private void mCurrentPositionUsingGPS(){

    }

}
