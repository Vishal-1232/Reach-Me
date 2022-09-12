package com.example.reachme.Models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Users {
    String profilePic,coverImage,userName,password,userID,lastMessage,mail,about,connectionStatus;
    long lastSeen;

    public Users(String profilePic, String coverImage, String userName, String password, String userID, String lastMessage, String mail, String about, String connectionStatus, long lastSeen) {
        this.profilePic = profilePic;
        this.coverImage = coverImage;
        this.userName = userName;
        this.password = password;
        this.userID = userID;
        this.lastMessage = lastMessage;
        this.mail = mail;
        this.about = about;
        this.connectionStatus = connectionStatus;
        this.lastSeen = lastSeen;
    }

    public Users(){};
//  sign up constructor
    public Users(String userName,String password,String mail,String connectionStatus){
        this.userName = userName;
        this.password = password;
        this.mail = mail;
        this.connectionStatus = connectionStatus;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserID(String key) {
        return userID;
    }
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(String connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
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
