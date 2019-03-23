package com.amandeep.abhichat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amandeep.abhichat.ChatActivity;
import com.amandeep.abhichat.Model.Chat;
import com.amandeep.abhichat.Model.Users;
import com.amandeep.abhichat.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MESSAGE_TYPE_LEFT=0;
    public static final int MESSAGE_TYPE_RIGHT=1;


    public Context mcontext;
    private List<Chat> mChat;
    private String imageUrl;
    String userID;
    FirebaseUser fuser;
    boolean textmessagetype= true;


    public MessageAdapter(Context context, List<Chat> mChat, String imageUrl) {
        this.mcontext = context;
        this.mChat = mChat;
        this.imageUrl=imageUrl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType==MESSAGE_TYPE_RIGHT) {
            View view = LayoutInflater.from(mcontext).inflate(R.layout.chat_item_right, viewGroup, false);

            return new MessageAdapter.ViewHolder(view);
        }
        else {
            View view=LayoutInflater.from(mcontext).inflate(R.layout.chat_item_left,viewGroup,false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder viewHolder, int position) {

        Chat chat=mChat.get(position);
        viewHolder.show_msg.setText(chat.getMessage());

        /*if (imageUrl.equals("default"))
        {
            viewHolder.profileimage.setImageResource(R.mipmap.ic_launcher);
        }
        else
        {
            Glide.with(mcontext).load(imageUrl).into(viewHolder.profileimage);
        }*/

       /* String from_user= chat.getReceiver();
        DatabaseReference mdatabaseref= FirebaseDatabase.getInstance().getReference().child("User").child(from_user);
        mdatabaseref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name= dataSnapshot.child("name").getValue().toString();
                String image=dataSnapshot.child("thumb_image").getValue().toString();
                viewHolder.show_msg.setText(name);
                Picasso.with(viewHolder.profileimage.getContext()).load(image).placeholder
                        (R.drawable.photo_picker).into(viewHolder.profileimage);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (textmessagetype)
        {
            viewHolder.show_msg.setText(chat.getMessage());
            viewHolder.imagemessge.setVisibility(View.INVISIBLE);
        }
        else {
            viewHolder.show_msg.setVisibility(View.VISIBLE);
            Picasso.with(viewHolder.profileimage.getContext()).load(chat.getMessage()).placeholder(R.drawable.photo_picker).into(viewHolder.imagemessge);

        }*/

    }


    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView show_msg;
        public ImageView profileimage;
        public  ImageView imagemessge;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_msg=itemView.findViewById(R.id.show_message);
            profileimage=itemView.findViewById(R.id.profile_img);
            imagemessge=itemView.findViewById(R.id.image_message_view);

        }


    }

    @Override
    public int getItemViewType(int position) {

        fuser=FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fuser.getUid()))
        {
            return MESSAGE_TYPE_RIGHT;
        }
        else {
            return MESSAGE_TYPE_LEFT;
        }
    }
}
