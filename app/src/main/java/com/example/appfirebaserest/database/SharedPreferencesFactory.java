package com.example.appfirebaserest.database;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.appfirebaserest.core.Constants;
import static android.content.Context.MODE_PRIVATE;

public class SharedPreferencesFactory {

    private String token;
    private SharedPreferences pref;
    private SharedPreferences.Editor prefEditor;

    public void saveToken(Context context, String token){
        pref = context.getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        prefEditor = pref.edit();
        prefEditor.putString("token", token);
        prefEditor.apply();
    }

    public String getToken(Context context){
        pref = context.getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        token = pref.getString("token", null);
        return token;
    }

    public void deletePreferences(Context context){
        pref = context.getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        prefEditor = pref.edit();
        prefEditor.clear();
        prefEditor.apply();
    }

}
