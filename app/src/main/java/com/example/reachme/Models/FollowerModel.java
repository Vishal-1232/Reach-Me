package com.example.reachme.Models;

public class FollowerModel {
    private String Id;
    private long followedAt;

    public FollowerModel() {
    }

    public FollowerModel(String id, long followedAt) {
        Id = id;
        this.followedAt = followedAt;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public long getFollowedAt() {
        return followedAt;
    }

    public void setFollowedAt(long followedAt) {
        this.followedAt = followedAt;
    }
}
