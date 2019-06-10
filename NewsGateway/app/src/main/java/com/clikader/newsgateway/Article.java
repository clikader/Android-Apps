package com.clikader.newsgateway;

import java.io.Serializable;

public class Article implements Serializable {
    private static final String TAG = "Article";

    private String author;
    private String title;
    private String description;
    private String url;
    private String imgUrl;
    private String time;

    public Article(String author, String title, String des, String url, String imgUrl, String time) {
        this.author = author;
        this.title = title;
        this.description = des;
        this.url = url;
        this.imgUrl = imgUrl;
        this.time = time;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getTime() {
        return time;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return author + " " + title + " " + url + " " + imgUrl + " " + time;
    }
}
