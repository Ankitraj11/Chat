package com.example.chat;

public class Userdata {

  String name;
  String status;
  String image;
  String userId;

    //public Userdata(String name, String status) {
      //  this.name = name;
        //this.status = status;
      //  this.image=image;
   // }


    public Userdata() {
    }

    public Userdata(String name, String status, String userId, String image) {
        this.name = name;
        this.status = status;
        this.userId=userId;
        this.image=image;

    }

    public Userdata(String name, String status, String userId) {
        this.name = name;
        this.status = status;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
