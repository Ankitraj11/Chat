package com.example.chat;

public class RequestedUserModel {

String name;
String image;
String userId;
String status;
    public RequestedUserModel() {
    }

    public RequestedUserModel(String name, String image,String userid) {
        this.name = name;
        this.image = image;
        userId=userid;
    }

    public String getUserid() {
        return userId;
    }

    public void setUserid(String userid) {
        userId = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
