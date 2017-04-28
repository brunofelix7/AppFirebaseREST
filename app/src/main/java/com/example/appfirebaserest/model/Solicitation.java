package com.example.appfirebaserest.model;

import java.util.List;

public class Solicitation {

    //private List<Double> location;
    private Double latitude;
    private Double longitude;
    private String urgency;
    private String status;
    private String date;

    public Solicitation(){

    }

    public Solicitation(Double latitude, Double longitude, String urgency, String status, String date) {
        super();
        this.latitude = latitude;
        this.longitude = longitude;
        this.urgency = urgency;
        this.status = status;
        this.date = date;
    }

    @Override
    public String toString() {
        return "Solicitation{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", urgency='" + urgency + '\'' +
                ", status='" + status + '\'' +
                ", date=" + date +
                '}';
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
