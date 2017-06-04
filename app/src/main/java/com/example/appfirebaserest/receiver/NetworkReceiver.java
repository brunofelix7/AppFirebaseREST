package com.example.appfirebaserest.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;
import com.example.appfirebaserest.core.Constants;
import com.example.appfirebaserest.util.CheckNetworkConnection;

public class NetworkReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int[] type = {ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_WIFI};
        if(CheckNetworkConnection.isNetworkAvailable(context, type)){
            Log.d(Constants.TAG, "Conectado");
        }else{
            Log.d(Constants.TAG, "Sem internet");
        }
    }
}
