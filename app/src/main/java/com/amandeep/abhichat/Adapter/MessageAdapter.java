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

import com.amandeep.abhichat.Model.Chat;
import com.amandeep.abhichat.R;
import com.amandeep.abhichat.StartActivity;
import com.amandeep.abhichat.VideoviewPly;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MESSAGE_TYPE_LEFT = 0;
    public static final int MESSAGE_TYPE_RIGHT = 1;
    public static final int IMAGE_MESSAGE_TYPE_LEFT = 3;
    public static final int IMAGE_MESSAGE_TYPE_RIGHT = 4;


    public Context mcontext;
    private List<Chat> mChat;
    String imageUrl;
    String userID;
    FirebaseUser fuser;
    boolean textmessagetype = true;
    int p;
    private String videoUrl;


    public MessageAdapter(Context context, List<Chat> mChat, String imageUrl) {
        this.mcontext = context;
        this.mChat = mChat;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == MESSAGE_TYPE_RIGHT) {

            View view = LayoutInflater.from(mcontext).inflate(R.layout.chat_item_right, viewGroup, false);
            return new MessageAdapter.ViewHolder(view);
        } else if (viewType == MESSAGE_TYPE_LEFT) {
            View view = LayoutInflater.from(mcontext).inflate(R.layout.chat_item_left, viewGroup, false);
            return new MessageAdapter.ViewHolder(view);
        } else if (viewType == IMAGE_MESSAGE_TYPE_RIGHT) {
            View view = LayoutInflater.from(mcontext).inflate(R.layout.image_message_view_right, viewGroup, false);
            Log.d("message right", "i am image right");
            return new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mcontext).inflate(R.layout.image_message_view_left, viewGroup, false);
            Log.d("message left", "i am image left");

            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.ViewHolder viewHolder, int position) {

        final Chat chat = mChat.get(position);
        //  imageUrl=chat.getImagemessgae();
        if (chat.getMessage_img()!=null){
            Glide.with(mcontext).load(chat.getMessage_img()).apply(new RequestOptions().override(200,100)).fitCenter().into(viewHolder.image_messge);
           viewHolder.mTimeStamp.setText(convertTimeStamp(chat.getTimestamp()));
        }
        if (chat.getMessage()!=null) {
            viewHolder.show_msg.setText(chat.getMessage());
        viewHolder.mTimeStamp.setText(convertTimeStamp(chat.getTimestamp()));

        }
        if (chat.getVideoUrl()!=null){
            if (chat.getMessage_img()!=null){
                viewHolder.image_messge.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        videoUrl=chat.getVideoUrl();
                        Log.d("snap",videoUrl);
                        Toast.makeText(mcontext, videoUrl, Toast.LENGTH_LONG).show();
                        Intent  intent_videoview= new Intent(mcontext, VideoviewPly.class);
                        intent_videoview.putExtra("videourl",videoUrl);
                        mcontext.startActivity(intent_videoview);

                    }
                });

            }

        }
        if (chat.getMessage_img()!=null && chat.getVideoUrl()==null)
        {

                viewHolder.image_messge.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      //  Toast.makeText(mcontext, "i am pure image", Toast.LENGTH_LONG).show();

                    }
                });


        }


    }


    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView show_msg;
        public ImageView profileimage;
        public ImageView image_messge;
        private TextView mTimeStamp;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_msg = itemView.findViewById(R.id.show_message);
            profileimage = itemView.findViewById(R.id.profile_img);
            image_messge = itemView.findViewById(R.id.iv_show_gallary_image);
            mTimeStamp=itemView.findViewById(R.id.time_stamp);


        }


    }

    @Override
    public int getItemViewType(int position) {

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        Chat chat = mChat.get(position);

        if (chat.getMessage() != null)
        {
            if (mChat.get(position).getSender().equals(fuser.getUid())) {
                return MESSAGE_TYPE_RIGHT;
            } else {
                return MESSAGE_TYPE_LEFT;
            }
        }
        else if (chat.getMessage_img()!=null)
        {
            if (mChat.get(position).getSender().equals(fuser.getUid())) {

                return IMAGE_MESSAGE_TYPE_RIGHT;
            } else {
                return IMAGE_MESSAGE_TYPE_LEFT;
            }
        }
        return position;
    }
    private String convertTimeStamp(String timeStamp){
        long longTemp=Long.parseLong(timeStamp);
        Date d = new Date((long)longTemp*1000);
        DateFormat f = new SimpleDateFormat("hh:mm:a");
        System.out.println(f.format(d));

        return f.format(d);
    }
}



