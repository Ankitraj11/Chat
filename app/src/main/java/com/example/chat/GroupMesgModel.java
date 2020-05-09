package com.example.chat;

public class GroupMesgModel {

String name;
String messgae;

    public GroupMesgModel() {
    }

    public GroupMesgModel(String name, String messgae) {
        this.name = name;
        this.messgae = messgae;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessgae() {
        return messgae;
    }

    public void setMessgae(String messgae) {
        this.messgae = messgae;
    }
}
