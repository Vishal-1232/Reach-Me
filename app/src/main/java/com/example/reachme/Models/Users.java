package com.example.reachme.Models;

public class Users {
    String profilePic,userName,password,userID,lastMessage,lastSeen,mail;

    public Users(String profilePic, String userName, String password, String userID, String lastMessage, String lastSeen,String mail) {
        this.profilePic = profilePic;
        this.userName = userName;
        this.password = password;
        this.userID = userID;
        this.lastMessage = lastMessage;
        this.lastSeen = lastSeen;
        this.mail = mail;
    }
    public Users(){};
//  sign up constructor
    public Users(String userName,String password,String mail){
        this.userName = userName;
        this.password = password;
        this.mail = mail;
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

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
