package com.example.reachme.Models;

public class NotificationModel {
    private String notificationId;
    private String notificationBy;
    private String notificationType;
    private long time;
    private boolean checkOpen=false;
    private String postId;
    private String postedBy;

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getNotificationBy() {
        return notificationBy;
    }

    public void setNotificationBy(String notificationBy) {
        this.notificationBy = notificationBy;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isCheckOpen() {
        return checkOpen;
    }

    public void setCheckOpen(boolean checkOpen) {
        this.checkOpen = checkOpen;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }
}
