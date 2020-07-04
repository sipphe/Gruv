package com.gruv.models;

import java.io.Serializable;

public class Venue implements Serializable {
    private String venueName;
    private String address;
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

    public Venue(String venueName, String address) {
        this.venueName = venueName;
        this.address = address;
    }

    public Venue(String venueName, String address, double latitude, double longitude) {
        this.venueName = venueName;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public void setLatLong(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
