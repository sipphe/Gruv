package com.gruv.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Event {

    private String eventId, eventName, eventDescription;
    private Author author;
    private LocalDate eventDate;
    private ArrayList<Comment> comments;
    private ArrayList<String> likes;
    private Integer imagePostId;

    public Event() {
    }

    public Event(String eventId, String eventName, Author author, LocalDate eventDate, String eventDescription, ArrayList<Comment> comments, List<String> likes, Integer imagePostId) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.author = author;
        this.eventDate = eventDate;
        this.eventDescription = eventDescription;
        this.comments = comments;
        this.imagePostId = imagePostId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthorName(Author author) {
        this.author = author;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<String> likes) {
        this.likes = likes;
    }

    public void addLike(String author) {
        this.likes.add(author);
    }

    public Integer getImagePostId() {
        return imagePostId;
    }

    public void setImagePostId(Integer imagePostId) {
        this.imagePostId = imagePostId;
    }
}
