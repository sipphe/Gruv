package com.gruv.models;

import androidx.annotation.NonNull;

import com.stfalcon.chatkit.commons.models.IUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Author implements IUser, Serializable {
    private String id, email, name, avatar, bio, site;
    private Integer profilePictureId;
    private boolean verified = false;
    private List<String> events, promotedEvents, following, followers;
    private int followingCount;

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

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
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

    public List<String> getEvents() {
        return events;
    }

    public void setEvents(List<String> events) {
        this.events = events;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public List<String> getPromotedEvents() {
        return promotedEvents;
    }

    public void setPromotedEvents(List<String> promotedEvents) {
        this.promotedEvents = promotedEvents;
    }

    public List<String> getFollowing() {
        return following;
    }

    public void setFollowing(List<String> following) {
        this.following = following;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }

    public int getFollowingCount() {
        if (following != null)
            return following.size() - 1;
        else
            return 0;
    }

    public void addEvent(String eventId) {
        if (this.events == null)
            this.events = new ArrayList<>();


        this.events.add(eventId);
    }
}