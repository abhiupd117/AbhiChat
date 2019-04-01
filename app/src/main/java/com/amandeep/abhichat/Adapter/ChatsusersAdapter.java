package com.amandeep.abhichat.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amandeep.abhichat.ChatActivity;
import com.amandeep.abhichat.Model.Users;
import com.amandeep.abhichat.R;
import com.amandeep.abhichat.StartActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ChatsusersAdapter extends RecyclerView.Adapter<ChatsusersAdapter.ViewHolder> {
    public Context mcontext;
    private List<Users> mUser;
    Intent chatlog_intent;
    FirebaseUser firebaseUser;
    String userID;
    FirebaseDatabase database;
    DatabaseReference myRef;
    String uId;


    public ChatsusersAdapter(Context context, List<Users> mUser) {
        this.mcontext = context;
        this.mUser = mUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(mcontext).inflate(R.layout.user_card_layout,viewGroup,false);
        userID= FirebaseAuth.getInstance().getUid();
        //userID=list_user_id;
        database=FirebaseDatabase.getInstance();
        myRef=database.getReference();

        return new ChatsusersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {

        final Users users=mUser.get(position);
        viewHolder.username.setText(users.getName());
        Log.e("usernamechats",users.getName());
        Glide.with(mcontext).load(users.getImageurl()).into(viewHolder.profileimage);
        final  String recieverId=users.getId();

        viewHolder.username.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View v) {

                final int layoutPosition = viewHolder.getLayoutPosition();
                Log.e("Selected_layoutPosition  ", String.valueOf(layoutPosition));
                final Users selectedUser = mUser.get(layoutPosition);

                chatlog_intent = new Intent(mcontext, ChatActivity.class);
                chatlog_intent.putExtra("selectedUser",selectedUser);
                chatlog_intent.putExtra("loggedUser",userID);
                Log.e("user",selectedUser.getName());

                Toast.makeText(mcontext,"Selected user is = "+selectedUser.getId()+" username = "+selectedUser.getName(),Toast.LENGTH_LONG).show();
                mcontext.startActivity(chatlog_intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView username;
        public ImageView profileimage;
        Intent chatlog_intent;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.name_on_user_card);
            profileimage=itemView.findViewById(R.id.user_card_img);
        }


    }
}
