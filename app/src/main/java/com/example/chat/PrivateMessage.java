package com.example.chat;

public class PrivateMessage {

private  String from,message,to,type;

    public PrivateMessage() {
    }

    public PrivateMessage(String from, String message, String to, String type) {
        this.from = from;
        this.message = message;
        this.to = to;
        this.type = type;
    }


    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
