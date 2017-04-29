package com.example.appfirebaserest.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Solicitation {

    private String firebaseId;
    private Double latitude;
    private Double longitude;
    private String urgency;
    private String status;
    private String date;

    public Solicitation(){

    }

    public Solicitation(String firebaseId, Double latitude, Double longitude, String urgency, String status, String date) {
        super();
        this.firebaseId = firebaseId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.urgency = urgency;
        this.status = status;
        this.date = date;
    }

    @Override
    public String toString() {
        return "Solicitation{" +
                "firebaseId='" + firebaseId + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", urgency='" + urgency + '\'' +
                ", status='" + status + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
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
