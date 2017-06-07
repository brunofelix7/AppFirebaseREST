package com.example.appfirebaserest.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import java.util.List;
import java.util.Locale;

public class GeocodingConvert {

    private static Geocoder geocoder;
    private static List<Address> listAddress;
    private static String address;

    public static String getAddress(Double lat, Double lng, Context context) {
        try {
            geocoder    = new Geocoder(context, Locale.getDefault());
            listAddress = geocoder.getFromLocation(lat, lng, 1);
            address     = listAddress.get(0).getAddressLine(0);
        } catch (Exception e) {
            return null;
        }
        return address;
    }
}
