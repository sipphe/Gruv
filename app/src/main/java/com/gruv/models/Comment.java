package com.gruv.models;

public class Comment extends Event {
    private String commentId, eventId, commentText;
    private Author author;

    public Comment() {

    }
    public Comment(String commentId, String eventId, String commentText, Author author) {
        this.commentId = commentId;
        this.eventId = eventId;
        this.commentText = commentText;
        this.author = author;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
}
