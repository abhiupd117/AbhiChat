package com.amandeep.abhichat.Model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Chat {
    private String sender;
    private String receiver;
    private String message=null;
    private String imageUrl=null;
    private String videoUrl=null;
    private String timestamp=null;
    private String lat= null;
    private String longitude=null;


    private  Users users;



    public Chat(String sender, String receiver, String message,String imageUrl,Users users,String videoUrl,String timestamp,String longitude) {
        this.sender = sender;
        this.longitude=longitude;
        this.receiver = receiver;
        this.message = message;
        this.imageUrl=imageUrl;
        this.videoUrl=videoUrl;
        this.timestamp=timestamp;
    }
    public Chat()
    {

    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getMessage_img() {
        return imageUrl;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public void setMessage_img(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSender() {
        return sender;
    }


    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
            return "Chat{" + "message=" + message + ", imageUrl=" + imageUrl  + ", message='" + message + '\'' + ", users=" + users + '}';
    }
}

