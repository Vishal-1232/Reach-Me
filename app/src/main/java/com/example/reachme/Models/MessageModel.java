package com.example.reachme.Models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MessageModel {
    String uid,message;
    long timeStamp;

    public MessageModel(String uid, String message, long timeStamp) {
        this.uid = uid;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    public MessageModel(String uid, String message) {
        this.uid = uid;
        this.message = message;
    }
    public MessageModel(){};

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getTimeDate(long timeStamp){
        try{
            Date netDate = (new Date(timeStamp));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
            return simpleDateFormat.format(netDate);
        }catch (Exception e){
            return "Time";
        }
    }
}
