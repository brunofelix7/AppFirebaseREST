package com.example.appfirebaserest.dao;

import com.example.appfirebaserest.model.Solicitation;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

public class SolicitationDAO extends SugarRecord<Solicitation>{

    private String firebaseId;
    private Double latitude;
    private Double longitude;
    private String urgency;
    private String status;
    private String date;

    @Ignore
    private Solicitation solicitation;

    public SolicitationDAO(){

    }

    public SolicitationDAO(String firebaseId, Double latitude, Double longitude, String urgency, String status, String date) {
        this.firebaseId = firebaseId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.urgency = urgency;
        this.status = status;
        this.date = date;
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

    public Solicitation getSolicitation() {
        return solicitation;
    }

    public void setSolicitation(Solicitation solicitation) {
        this.solicitation = solicitation;
    }
}
