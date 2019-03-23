package com.amandeep.abhichat;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amandeep.abhichat.Adapter.MessageAdapter;
import com.amandeep.abhichat.AppUtils.Constant;
import com.amandeep.abhichat.Model.Chat;
import com.amandeep.abhichat.Model.Users;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.amandeep.abhichat.R.layout.activity_chat;
@SuppressWarnings("unchecked")


public class ChatActivity extends AppCompatActivity {

    CircleImageView profileImage;
    TextView username;
    //FirebaseUser fuser;
    DatabaseReference reference;
    Intent intent;
    private FirebaseAuth firebaseAuth;
    Context context;
    ImageButton add_btn;
    EditText message_write;
    Button btn_message_send;
    private FirebaseAuth mAuth;
    private String mCurrentUser;
    private static final int GALLERY_PICK=1;
    public static final int REQUEST_PERMISSIONS_FOR_IMAGE = 111;
    public static final String IMAGE = "image";
    private static int RESULT_LOAD_IMAGE = 1;
    public Uri selectedImage;
    StorageReference storageReference;
    private String imagemessgeUrl;




    FirebaseStorage storage;

    StorageReference mImageStorageRef;

    String userId;
    MessageAdapter messageAdapter;
    private  RecyclerView recyclerView;
    private Users selectedUser;
    private boolean isFirstTime = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        profileImage=findViewById(R.id.profile_img);
        username=findViewById(R.id.usernme_for_chat);

        message_write=findViewById(R.id.message_box);
        add_btn=findViewById(R.id.btn_add_manymore);
        btn_message_send=findViewById(R.id.btn_message_sent);

        recyclerView= findViewById(R.id.chat_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);




        storage=FirebaseStorage.getInstance();
        mImageStorageRef=storage.getReference();
        intent = getIntent();
        //TODO this is selected user object. Need to play with this object
        selectedUser = (Users) intent.getSerializableExtra("selectedUser");
        //username = selectedUser.getName();
        username.setText(selectedUser.getName());//name of receiver
        userId = intent.getStringExtra("loggedUser");


       // fuser=FirebaseAuth.getInstance().getCurrentUser();
        //Log.e("djhfuihfuis", String.valueOf(fuser));

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        reference=FirebaseDatabase.getInstance().getReference("User").child(userId);

        btn_message_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(message_write != null){
                    //TODO send text
                    //TODO set image variable to null
                    String msg=message_write.getText().toString();
                    if (!msg.equals(""))
                    {
//                    Toast.makeText(context,"Receiver = "+fuser.getUid()+" = name = "+fuser.getDisplayName(),Toast.LENGTH_SHORT).show();
                        sendMessage(userId,selectedUser.getId(),msg);
                        Log.e("UserID",userId);
                    }
                    else
                    {
                        Toast.makeText(context,"Sorry, Can't sent empty emssage",Toast.LENGTH_LONG).show();
                        finish();

                    }
                    message_write.setText("");
                }else{
                    //TODO send image
                    //TODO set message text to null
                    //take another variable for image and send
                }

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Users users=dataSnapshot.getValue(Users.class);
                //username.setText(users.getName());

            //    Glide.with(context).load(users.get Imageurl()).into(profileImage);

                readMessage(userId,selectedUser.getId(), users.getImageurl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    private void sendMessage(String sender, String receiver, String message)
    {
        reference   = FirebaseDatabase.getInstance().getReference();
        Log.e("send Chat","called");
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        reference.child("messages").push().setValue(hashMap);

    }

    private void readMessage(final String myId, final String userId, final String imageurl)
    {

        reference=FirebaseDatabase.getInstance().getReference().child("messages");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final List<Chat> mChat = new ArrayList<>();
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                   // snapshot.
                    Chat chat=snapshot.getValue(Chat.class);
                    if( chat.getReceiver() != null && chat.getReceiver() != null) {
                        if (!chat.getReceiver().isEmpty() && !chat.getReceiver().isEmpty()) {
                            if (chat.getReceiver().equals(myId) && chat.getSender().equals(userId) || chat.getReceiver().equals(userId) && chat.getSender().equals(myId)) {
                               Log.e("Add Chat","called");

                                   mChat.add(chat);



                            }
                        }

                    }
                    messageAdapter =new MessageAdapter(ChatActivity.this,mChat,imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void requestImagePermissions() {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE,
                READ_EXTERNAL_STORAGE, CAMERA}, REQUEST_PERMISSIONS_FOR_IMAGE);
//        permissionListener.acceptedPermission();
    }
    public boolean checkImagePermission() {
        int result = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(this, CAMERA);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
                && result2 == PackageManager.PERMISSION_GRANTED;
    }

   /* protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView imageView = (ImageView) findViewById(R.id.select_img_for_profile);

            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                imageView.setImageBitmap(bmp);
                Log.d(TAG, "profile photo selected");
                //bmp = getBitmapFromUri(selectedImage);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.d(TAG, "profile photo not selected");

            }
            imageView.setImageBitmap(bmp);



            uplodeImagetoFireBaseDataBase();



        }

    }*/

    private void uplodeImagetoFireBaseDataBase() {
        //checking if file is available
        if (selectedImage != null) {

            //getting the storage reference
            final StorageReference sRef = storageReference.child(Constant.STORAGE_PATH_UPLOADS + System.currentTimeMillis() + "." + getFileExtension(selectedImage));

            UploadTask uploadTask=sRef.putFile(selectedImage);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful())
                    {

                        throw task.getException();
                    }
                    return sRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful())
                    {
                        Uri downlodeUri=task.getResult();
                        System.out.println("Uploaded"+downlodeUri);
                        if (downlodeUri!=null)
                        {
                            imagemessgeUrl=downlodeUri.toString();
                            Log.e("fdfdjkfh",imagemessgeUrl);

                        }
                    }
                    else
                    {
                        Log.d("Something_wrong", "Something wrong with user photo String link which is comming from DB");
                    }
                }
            });

        } else {
            //display an error if no file is selected
        }
    }
    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}
