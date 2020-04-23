package com.gruv.models;

import java.io.Serializable;

public class Like implements Serializable {
    String eventId;
    String likeId;
    Author author;

    public Like() {
    }

    public Like(String eventId, Author author) {
        this.eventId = eventId;
//        this.likeId = getId();
        this.author = author;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getLikeId() {
        return likeId;
    }


    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public void setLikeId(String likeId) {
        this.likeId = likeId;
    }

    public String getId() {
        int num;
        String txt = "";
        txt = "";
        for (int i = 0; i < 6; i++) {
            num = (int) ((Math.random() * (9 - 0) + 1));
            txt = txt + num;
        }

        return txt;
    }

}