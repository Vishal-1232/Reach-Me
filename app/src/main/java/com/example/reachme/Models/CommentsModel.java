package com.example.reachme.Models;

public class CommentsModel {
    private String commentId;
    private String commentedBy;
    private String commentBody;
    private long commentTime;

    public CommentsModel() {
    }

    public CommentsModel(String commentedBy, String commentBody, long commentTime) {
        this.commentedBy = commentedBy;
        this.commentBody = commentBody;
        this.commentTime = commentTime;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getCommentedBy() {
        return commentedBy;
    }

    public void setCommentedBy(String commentedBy) {
        this.commentedBy = commentedBy;
    }

    public String getCommentBody() {
        return commentBody;
    }

    public void setCommentBody(String commentBody) {
        this.commentBody = commentBody;
    }

    public long getCommentTime() {
        return commentTime;
    }

    public void setCommentetTime(long commentetTime) {
        this.commentTime = commentetTime;
    }
}
