package com.gruv.models;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Event implements Serializable {

    private String eventId, eventName, eventDescription;
    private Author author;
    private LocalDate eventDate;
    private Venue venue;
    private ArrayList<Comment> comments;
    private ArrayList<Like> likes;
    private Integer imagePostId;

    public Event() {
    }

    public Event(@NonNull String eventId, @NonNull String eventName, String eventDescription, Author author, @NonNull LocalDate eventDate, Venue venue, Integer imagePostId) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.author = author;
        this.eventDate = eventDate;
        this.venue = venue;
        this.imagePostId = imagePostId;
    }

    public Event(@NonNull String eventId, @NonNull String eventName, Author author, @NonNull LocalDate eventDate, Venue venue, String eventDescription, ArrayList<Comment> comments, List<Like> likes, Integer imagePostId) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.author = author;
        this.eventDate = eventDate;
        this.venue = venue;
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

    public void setAuthor(Author author) {
        this.author = author;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
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

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<Like> likes) {
        this.likes = likes;
    }

    public void addLike(Like like) {
        if (this.likes == null) {
            this.likes = new ArrayList<>();
        }
        this.likes.add(like);
    }

    public void removeLike(Like like) {
        if (this.likes != null)
            this.likes.remove(like);
    }

    public Integer getImagePostId() {
        return imagePostId;
    }

    public void setImagePostId(Integer imagePostId) {
        this.imagePostId = imagePostId;
    }
}
