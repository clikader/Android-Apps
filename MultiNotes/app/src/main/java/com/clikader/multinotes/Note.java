package com.clikader.multinotes;


import java.io.Serializable;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class Note implements Serializable{
    private String title;
    private String content;
    private String lastmodify;

    public Note(String nTitle, String nLastModify, String nContent) {
        this.title = nTitle;
        this.content = nContent;
        this.lastmodify = nLastModify;
    }

    public Note() {
        this.title = "";
        this.content = "";
        this.lastmodify = "";
    }

    public String getTitle() {return title;}
    public String getContent() {return content;}
    public String getLastmodify() {return lastmodify;}

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setLastmodify(String lastmodify) {
        this.lastmodify = lastmodify;
    }


    @Override
    public String toString() {
        return title + lastmodify + content;
    }
}
