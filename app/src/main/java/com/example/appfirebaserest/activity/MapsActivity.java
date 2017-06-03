package com.example.appfirebaserest.activity;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import com.example.appfirebaserest.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMaps;
    private CameraUpdate cameraUpdate;
    private MarkerOptions markerOptions;
    private Marker marker;
    private LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mapInit();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMaps = googleMap;
        double lat = -34;
        double lng = 151;
        goTolocation(lat, lng, 15f);
        setMarker("Australia", "Sydney", new LatLng(-34, 151));
    }

    /**
     * Inicialização do Google Maps
     */
    private void mapInit(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Move a câmera para a localização desejada
     */
    private void goTolocation(double latitude, double longitude, float zoom){
        latLng = new LatLng(latitude, longitude);
        cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        googleMaps.animateCamera(cameraUpdate);
    }

    /**
     * Adiciona um Marker no mapa
     */
    public void setMarker(String location, String snippet, LatLng latLng){
        //  Garante que vai existir apenas um marcador
        if(marker != null){
            marker.remove();
        }
        markerOptions = new MarkerOptions()
                .title(location)
                .position(latLng)
                .snippet(snippet)
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_user));
        marker = this.googleMaps.addMarker(markerOptions);
        marker.hideInfoWindow();
    }
}
