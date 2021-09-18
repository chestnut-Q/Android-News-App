package com.java.qinruoyu;

import java.io.Serializable;

public class News implements Serializable {

    private String[] image;
    private String publishTime;
    private String[] keywords;
    private String video;
    private String title;
    private String content;
    private String newsID;
    private String[] organizations;
    private String publisher;

    public News(String[] image, String publishTime, String[] keywords, String video, String title,
                String content, String newsID, String[] organizations, String publisher) {
        this.image = image;
        this.publishTime = publishTime;
        this.keywords = keywords;
        this.video = video;
        this.title = title;
        this.content = content;
        this.newsID = newsID;
        this.organizations = organizations;
        this.publisher = publisher;
    }

    public String[] getImage() {
        return image;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public String getVideo() {
        return video;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getNewsID() {
        return newsID;
    }

    public String[] getOrganizations() {
        return organizations;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setImage(String[] image) {
        this.image = image;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setNewsID(String newsID) {
        this.newsID = newsID;
    }

    public void setOrganizations(String[] organizations) {
        this.organizations = organizations;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String[] getKeywords() {
        return keywords;
    }

    public void setKeywords(String[] keywords) {
        this.keywords = keywords;
    }
}
