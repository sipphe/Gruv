package com.gruv.models;

import java.io.Serializable;

public class Venue implements Serializable {
    private String venueName;
    private double latitude;
    private double longitude;

    public Venue() {

    }

    public Venue(String venueName) {
        this.venueName = venueName;
    }

    public Venue(String venueName, double latitude, double longitude) {
        this.venueName = venueName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
