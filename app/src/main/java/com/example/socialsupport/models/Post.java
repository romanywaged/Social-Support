package com.example.socialsupport.models;

import com.google.firebase.database.ServerValue;

public class Post {
    private String tittle;
    private String postKey;
    private String description;
    private String picture;
    private String userId;
    private String userPhoto;
    private Object timeStamp;

    public Post(String tittle, String description, String picture, String userId, String userPhoto) {
        this.tittle = tittle;
        this.description = description;
        this.picture = picture;
        this.userId = userId;
        this.userPhoto = userPhoto;
        this.timeStamp = ServerValue.TIMESTAMP;
    }
    public Post()
    {}
    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getTittle() {
        return tittle;
    }

    public String getDescription() {
        return description;
    }

    public String getPicture() {
        return picture;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }
}
