package com.example.appfirebaserest.core;

/**
 * Classe usada para passar e pegar parêmetros de uma activity para a outra
 */
public class MainController {

    private static MainController instance = null;
    private Double lat;
    private Double lng;
    private String address;

    public MainController(){

    }

    public static MainController getInstance(){
        if(instance == null){
            instance = new MainController();
        }
        return instance;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
