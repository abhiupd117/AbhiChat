package com.amandeep.abhichat;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.nfc.Tag;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amandeep.abhichat.AppUtils.Constant;
import com.amandeep.abhichat.Model.Users;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.time.temporal.TemporalAdjuster;
import java.util.HashMap;
import java.util.UUID;

public class RegisterUserAcitivity extends AppCompatActivity {
    private Button rCreteAccountbtn;
    private EditText rUserName;
    private EditText rUserMail;
    private EditText rUserPassword;
    private ImageView selectImage_for_profile;

    private FirebaseAuth mAuth;
    private ProgressBar progressDialog;

    FirebaseStorage storage;
    private Uri filePathColumn;
    Context context;

    public Uri selectedImage;
    StorageReference storageReference;

    private DatabaseReference mDatabase, imgDBref;
    private FirebaseDatabase firebaseDatabase;

    private static int RESULT_LOAD_IMAGE = 1;
    private String TAG;
    Users users= new Users();
    private FirebaseUser user;

    String userPhotoStringLink;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user_acitivity);

        rUserName = findViewById(R.id.reg_username);
        rUserMail = findViewById(R.id.reg_mailaddress);
        rUserPassword = findViewById(R.id.reg_password);
        rCreteAccountbtn = findViewById(R.id.button_register);
        selectImage_for_profile = findViewById(R.id.select_img_for_profile);
        context=this;
        users= new Users();



        storage=FirebaseStorage.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference(Constant.DATABASE_PATH_UPLOADS);

        firebaseDatabase=FirebaseDatabase.getInstance();
        mDatabase=firebaseDatabase.getInstance().getReference(Constant.DATABSE_USER);


        storageReference=storage.getReference();
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressBar(this);

        selectImage_for_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });


        rCreteAccountbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_name = rUserName.getText().toString();
                String user_mail = rUserMail.getText().toString();
                String password = rUserPassword.getText().toString();
               String imageURI=userPhotoStringLink;
               Log.e("Uploaded image url = ",userPhotoStringLink);


                if (!TextUtils.isEmpty(user_name) || !TextUtils.isEmpty(user_mail) || !TextUtils.isEmpty(password)) {


                   /* progressDialog.setTitle("Registering...");
                    progressDialog.setMessage("wait while creating yout account");
                    progressDialog.show();*/
                   progressDialog.setMax(100);
                    register_user(user_name, user_mail, password,imageURI);
                    Log.d(TAG, "profile created");


                } else {
                    Toast.makeText(RegisterUserAcitivity.this, "please Fill All field", Toast.LENGTH_LONG).show();
                }

            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();
        user = mAuth.getCurrentUser();
        if (user != null) {
        } else {
            signInAnonymously();
        }
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(this, new  OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
            }
        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e(TAG, "signInAnonymously:FAILURE", exception);
                    }
                });
    }

    private void register_user(final String user_name, final String user_mail, String password, final String imageURI)
    {
        mAuth.createUserWithEmailAndPassword(user_mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser currentuser=FirebaseAuth.getInstance().getCurrentUser();
                    String uid= currentuser.getUid();
                    mDatabase=FirebaseDatabase.getInstance().getReference().child("User").child(uid);
                    HashMap<String, String>  userMap= new HashMap<>();
                    userMap.put("name",user_name);
                    userMap.put("email",user_mail);

                    userMap.put("image_url", imageURI);

                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful())
                            {
                              //  progressDialog.dismiss();
                                Intent mainIntent = new Intent(RegisterUserAcitivity.this, MainActivity.class);
                                startActivity(mainIntent);
                                finish();
                            }
                        }
                    });

                } else
                {
                    Toast.makeText(RegisterUserAcitivity.this, "Can't sign in, please check and try again", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
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

    }


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
                                userPhotoStringLink=downlodeUri.toString();
                                Log.e("fdfdjkfh",userPhotoStringLink);

                            }
                        }
                        else
                        {
                            Log.d(TAG, "Something wrong with user photo String link which is coming from DB");
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
