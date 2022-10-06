package com.example.reachme.Models;

public class Status {
    private String imageURL;
    private long timestamp;

    public Status() {
    }

    public Status(String imageURL, long timestamp) {
        this.imageURL = imageURL;
        this.timestamp = timestamp;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
