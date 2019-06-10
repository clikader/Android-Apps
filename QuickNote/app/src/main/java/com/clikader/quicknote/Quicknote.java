package com.clikader.quicknote;

/**
 * Created by clikader on 2018/1/27.
 */

public class Quicknote {
    private String savedTime;
    private String savedNote;

    public String getSavedTime() {
        return savedTime;
    }

    public String getSavedNote() {
        return savedNote;
    }

    public void setSavedTime(String time) {
        this.savedTime = time;
    }

    public void setSavedNote(String note) {
        this.savedNote = note;
    }
}
