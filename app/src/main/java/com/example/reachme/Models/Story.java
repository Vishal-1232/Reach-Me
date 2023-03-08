package com.example.reachme.Models;


import java.util.ArrayList;

public class Story {
    private String storyBy;
    private long storyAt;
    private ArrayList<UserStoriesModel> storiesList;

    public Story() {
    }

    public Story(String storyBy, long storyAt, ArrayList<UserStoriesModel> storiesList) {
        this.storyBy = storyBy;
        this.storyAt = storyAt;
        this.storiesList = storiesList;
    }

    public String getStoryBy() {
        return storyBy;
    }

    public void setStoryBy(String storyBy) {
        this.storyBy = storyBy;
    }

    public long getStoryAt() {
        return storyAt;
    }

    public void setStoryAt(long storyAt) {
        this.storyAt = storyAt;
    }

    public ArrayList<UserStoriesModel> getStoriesList() {
        return storiesList;
    }

    public void setStoriesList(ArrayList<UserStoriesModel> storiesList) {
        this.storiesList = storiesList;
    }
}

