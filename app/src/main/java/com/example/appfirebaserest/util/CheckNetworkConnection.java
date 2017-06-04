package com.example.appfirebaserest.util;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Classe que verifica se existe conexão com a internet
 */
public class CheckNetworkConnection {

    public static boolean isNetworkAvailable(Context context, int[] typeNetworks) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);
            for (int typeNetwork : typeNetworks) {
                NetworkInfo networkInfo = connectivityManager.getNetworkInfo(typeNetwork);
                if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
