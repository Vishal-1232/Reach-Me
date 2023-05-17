package com.example.reachme.Models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MessageModel {
    String uid, message, messageId;
    long timeStamp;
    int feeling = -1;
    boolean downloaded = false;
    String type = "Text", photo="";

    public boolean isDownloaded() {
        return downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public MessageModel(String uid, String message, String messageId, long timeStamp, int feeling) {
        this.uid = uid;
        this.message = message;
        this.messageId = messageId;
        this.timeStamp = timeStamp;
        this.feeling = feeling;
    }

    public MessageModel(String uid, String message) {
        this.uid = uid;
        this.message = message;
    }

    public MessageModel() {
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

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

    public int getFeeling() {
        return feeling;
    }

    public void setFeeling(int feeling) {
        this.feeling = feeling;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimeDate(long timeStamp) {
        try {
            Date netDate = (new Date(timeStamp));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
            return simpleDateFormat.format(netDate);
        } catch (Exception e) {
            return "Time";
        }
    }
}
