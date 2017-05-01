package com.example.appfirebaserest.util;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Classe que verifica se existe conexão com a internet
 */
public class CheckNetworkConnection {

    private Context context;

    public CheckNetworkConnection(Context context){
        this.context = context;
    }

    //  RETORNA True SE TIVER CONEXÃO COM A INTERNET, E False CASO NÃO
    public boolean isConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);
        if(connectivityManager != null){
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null){
                if(networkInfo.getState() == NetworkInfo.State.CONNECTED){
                    return true;
                }
            }
        }
        return false;
    }

}
