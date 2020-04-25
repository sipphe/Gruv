package com.gruv.models;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

public class Event implements Serializable, Comparable<Event> {

    private String eventID, eventName, eventDescription;
    private Author author;
    private LocalDateTime eventDate;
    private Venue venue;
    private HashMap<String, Comment> comments;
    private HashMap<String, Like> likes;
    private Integer imagePostId;
    private String eventDateString;
    private String imagePostUrl;
    private String datePosted;

    public Event() {
    }

    public Event(@NonNull String eventID, @NonNull String eventName, String eventDescription, Author author, @NonNull LocalDateTime eventDate, Venue venue, Integer imagePostId) {
        this.eventID = eventID;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.author = author;
        this.eventDate = eventDate;
        this.venue = venue;
        this.imagePostId = imagePostId;
    }

    public Event(@NonNull String eventID, @NonNull String eventName, Author author, @NonNull LocalDateTime eventDate, Venue venue, String eventDescription, HashMap<String, Comment> comments, List<Like> likes, Integer imagePostId) {
        this.eventID = eventID;
        this.eventName = eventName;
        this.author = author;
        this.eventDate = eventDate;
        this.venue = venue;
        this.eventDescription = eventDescription;
        this.comments = comments;
        this.imagePostId = imagePostId;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
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

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public void convertDate(String eventDateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        this.eventDate = LocalDateTime.parse(eventDateString, formatter);
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

    public HashMap<String, Comment> getComments() {
        return comments;
    }

    public void setComments(HashMap<String, Comment> comments) {
        this.comments = comments;
    }

    public void addComment(Comment comment) {
        if (comments != null) {
            this.comments.put(comment.getCommentId(), comment);
        } else {
            this.comments = new HashMap<>();
            this.comments.put(comment.getCommentId(), comment);
        }
    }

    public HashMap<String, Like> getLikes() {
        return likes;
    }

    public void setLikes(HashMap<String, Like> likes) {
        this.likes = likes;
    }

    public void addLike(Like like) {
        if (this.likes == null) {
            this.likes = new HashMap<>();
        }
        this.likes.put(like.getLikeId(), like);
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

    public String getEventDateString() {
        return eventDateString;
    }

    public void setEventDateString(String eventDateString) {
        convertDate(eventDateString);
        this.eventDateString = eventDateString;
    }

    public String getImagePostUrl() {
        return imagePostUrl;
    }

    public String getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(String datePosted) {
        this.datePosted = datePosted;
    }

    public void setImagePostUrl(String imagePostUrl) {
        this.imagePostUrl = imagePostUrl;
    }

    @Override
    public String toString() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode())
                + ", " + eventName
                + ", by " + author.getName();
    }

    @Override
    public int compareTo(Event o) {
        return datePosted.compareTo(o.datePosted);
    }

    public void clearComments() {
        comments.clear();
    }
}
