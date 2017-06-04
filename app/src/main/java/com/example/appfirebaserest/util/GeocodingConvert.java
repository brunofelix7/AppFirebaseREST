package com.example.appfirebaserest.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import java.util.List;
import java.util.Locale;

public class GeocodingConvert {

    private static Geocoder geocoder;
    private static List<Address> listAddress;
    private static Address address;
    private static String location;

    public static String getAddress(double lat, double lng, Context context) {
        try {
            geocoder    = new Geocoder(context, Locale.getDefault());
            listAddress = geocoder.getFromLocation(lat, lng, 1);
            address     = listAddress.get(0);
            location    = address.getAddressLine(0);
        } catch (Exception e) {
            return null;
        }
        return location;
    }
}
