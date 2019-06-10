package com.clikader.knowyourgovernment;

import java.io.Serializable;

public class Governor implements Serializable{
    private static final String TAG = "Governor";

    private String position;
    private String name;
    private String party;
    private String address;
    private String phone;
    private String email;
    private String website;
    private String facebook;
    private String twitter;
    private String googleplus;
    private String youtube;
    private String photo;

    public Governor(String position, String name) {
        this.position = position;
        this.name = name;
    }

    public Governor(String position, String name, String party, String address, String phone, String email,
        String website, String facebook, String twitter, String googleplus, String youtube, String photo) {
        this.position = position;
        this.name = name;
        this.party = party;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.website = website;
        this.facebook = facebook;
        this.twitter = twitter;
        this.googleplus = googleplus;
        this.youtube = youtube;
        this.photo = photo;
    }

    public String getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    public String getParty() {
        return party;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getWebsite() {
        return website;
    }

    public String getFacebook() {
        return facebook;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getGoogleplus() {
        return googleplus;
    }

    public String getYoutube() {
        return youtube;
    }

    public String getPhoto() {return photo;}

    public void setPosition(String position) {
        this.position = position;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public void setGoogleplus(String googleplus) {
        this.googleplus = googleplus;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public void setPhoto(String photo) {this.photo = photo;}

    @Override
    public String toString() {
        return position + " " + name + " " + party + " " + address + " " + phone + " " + email + " " + website;
    }
}
