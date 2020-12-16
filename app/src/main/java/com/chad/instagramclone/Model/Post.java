package com.chad.instagramclone.Model;

public class Post {

    private String postId;
    private String postImage;
    private String caption;
    private String publisherId;

    public Post() {}

    public Post(String postId, String postImage, String caption, String publisherId) {
        this.postId = postId;
        this.postImage = postImage;
        this.caption = caption;
        this.publisherId = publisherId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }
}
