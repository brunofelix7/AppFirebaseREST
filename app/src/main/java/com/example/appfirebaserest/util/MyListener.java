package com.example.appfirebaserest.util;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import com.example.appfirebaserest.core.Constants;

public class MyListener implements LocationListener{

    private double lat;
    private double lng;

    @Override
    public void onLocationChanged(Location location) {
        if(location != null){
            lat = location.getLatitude();
            lng = location.getLongitude();
            Log.d(Constants.TAG, "Lat: " + lat);
            Log.d(Constants.TAG, "Lng: " + lng);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
