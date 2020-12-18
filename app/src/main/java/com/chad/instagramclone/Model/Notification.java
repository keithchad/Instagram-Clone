package com.chad.instagramclone.Model;

public class Notification {

    private String userId;
    private String postId;
    private String textComment;
    private boolean isPost;

    public Notification() {}

    public Notification(String userId, String postId, String textComment, boolean isPost) {
        this.userId = userId;
        this.postId = postId;
        this.textComment = textComment;
        this.isPost = isPost;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getTextComment() {
        return textComment;
    }

    public void setTextComment(String textComment) {
        this.textComment = textComment;
    }

    public boolean isPost() {
        return isPost;
    }

    public void setPost(boolean post) {
        isPost = post;
    }
}
