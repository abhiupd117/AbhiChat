package com.amandeep.abhichat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {

    private Button mRegBtn, mLoginBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mRegBtn=findViewById(R.id.button_to_register_activity);
        mLoginBtn=findViewById(R.id.button_to_login_activity);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_login= new Intent(StartActivity.this,UserLoginActivity.class);
                startActivity(intent_login);
            }
        });
        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_regitser= new Intent(StartActivity.this, RegisterUserAcitivity.class);
                startActivity(intent_regitser);
            }
        });
    }

}
