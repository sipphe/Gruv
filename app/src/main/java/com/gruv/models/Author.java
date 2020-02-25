package com.gruv.models;

import androidx.annotation.NonNull;

import com.stfalcon.chatkit.commons.models.IUser;

import java.io.Serializable;
import java.util.Map;

public class Author implements IUser, Serializable {
    private String id, email, name, avatar;
    private Integer profilePictureId;
    private Map<String, String> events;

    public Author() {

    }

    public Author(@NonNull String id, String name, String avatar, Integer profilePictureId) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.profilePictureId = profilePictureId;
    }
    /*...*/

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getProfilePictureId() {
        return profilePictureId;
    }

    public void setProfilePictureId(Integer profilePictureId) {
        this.profilePictureId = profilePictureId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, String> getEvents() {
        return events;
    }

    public void setEvents(Map<String, String> events) {
        this.events = events;
    }
}