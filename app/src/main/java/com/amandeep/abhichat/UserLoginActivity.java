package com.amandeep.abhichat;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amandeep.abhichat.AppUtils.MyAppPref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class UserLoginActivity extends AppCompatActivity {
    EditText username_for_login_editText, password_for_login_editText;
    Button login_btn;
    ProgressBar loginprogress;
    private FirebaseAuth mAuth;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        username_for_login_editText=findViewById(R.id.username_for_login);
        password_for_login_editText=findViewById(R.id.password_for_login);
        login_btn=findViewById(R.id.login_button_btn);
        mAuth=FirebaseAuth.getInstance();
        mContext = UserLoginActivity.this;
        loginprogress= new ProgressBar(this);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username= username_for_login_editText.getText().toString();
                final String password= password_for_login_editText.getText().toString();
                if (!TextUtils.isEmpty(username) || !TextUtils.isEmpty(password))
                {
                    loginprogress.setMax(100);
                   /* loginprogress.s("Please wait while we check your credentils.");
                    loginprogress.setCanceledOnTouchOutside(false);
                    loginprogress.show()*/;

                    FirebaseInstanceId.getInstance().getInstanceId()
                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                @Override
                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                    if (!task.isSuccessful()) {
                                        Log.w("log", "getInstanceId failed", task.getException());
                                        return;
                                    }

                                    // Get new Instance ID token
                                    String token = task.getResult().getToken();

                                    // Log and toast
                                    // String msg = getString(R.string.msg_token_fmt, token);
                                    Log.d("log", token);

                                    loginUser(username,password,token);
                                    Log.d("log", "profile created");
                                    // Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            });


                }
                else
                {

                    Toast.makeText(UserLoginActivity.this, "Can't sign in, please check and try again",Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private void loginUser(final String username, String password, final String token) {
        mAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                   // loginprogress.dismiss();

                    MyAppPref myAppPref = new MyAppPref(mContext);
                    myAppPref.setAccessToken(token);
                    myAppPref.setUserName(username);


                    Intent mainIntent= new Intent(UserLoginActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
                else
                {
                   // loginprogress.hide();
                    Toast.makeText(UserLoginActivity.this,"Something went wrong",Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
