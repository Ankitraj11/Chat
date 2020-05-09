package com.example.chat;

public class FriendsModel {


    String friendname;
    String friendimage;
    String status;
    String userid;


    public FriendsModel() {
    }

    public FriendsModel(String friendname, String friendimage,String status,String userid) {
        this.friendname = friendname;
        this.friendimage = friendimage;
        this.status=status;
        this.userid=userid;

    }

    public FriendsModel(String friendname, String status, String userid) {
        this.friendname = friendname;
        this.status = status;
        this.userid = userid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFriendname() {
        return friendname;
    }

    public void setFriendname(String friendname) {
        this.friendname = friendname;
    }

    public String getFriendimage() {
        return friendimage;
    }

    public void setFriendimage(String friendimage) {
        this.friendimage = friendimage;
    }
}
