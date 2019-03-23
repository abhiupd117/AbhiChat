package com.amandeep.abhichat.Model;

import java.io.Serializable;

public class Users implements Serializable {

    public String image_url;
    public String username;
    public  String name;
    private String id;

    private String email;

    public Users()
    {

    }
    public Users(String imageurl,String username, String name,String id)
    {
        this.image_url=imageurl;
        this.name=name;
        this.username=username;
        this.id=id;
    }

    public String getImageurl() {
        return image_url;
    }

    public void setImageurl(String imageurl) {
        this.image_url = imageurl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
