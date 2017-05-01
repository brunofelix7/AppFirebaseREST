package com.example.appfirebaserest.model;

import com.google.firebase.database.IgnoreExtraProperties;
import java.io.Serializable;

/**
 * Classe modelo que representa o meu JSON no Firebase
 */
@IgnoreExtraProperties
public class Solicitation implements Serializable{

    private String firebaseId;
    private Double latitude;
    private Double longitude;
    private String urgency;
    private String nivel_consciencia;
    private String nivel_respiracao;
    private String status;
    private String date;

    public Solicitation(){

    }

    public Solicitation(Double latitude, Double longitude, String urgency, String nivel_consciencia, String nivel_respiracao, String status, String date) {
        super();
        this.latitude = latitude;
        this.longitude = longitude;
        this.urgency = urgency;
        this.nivel_consciencia = nivel_consciencia;
        this.nivel_respiracao = nivel_respiracao;
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
                ", nivel_consciencia='" + nivel_consciencia + '\'' +
                ", nivel_respiracao='" + nivel_respiracao + '\'' +
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

    public String getNivel_consciencia() {
        return nivel_consciencia;
    }

    public void setNivel_consciencia(String nivel_consciencia) {
        this.nivel_consciencia = nivel_consciencia;
    }

    public String getNivel_respiracao() {
        return nivel_respiracao;
    }

    public void setNivel_respiracao(String nivel_respiracao) {
        this.nivel_respiracao = nivel_respiracao;
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
