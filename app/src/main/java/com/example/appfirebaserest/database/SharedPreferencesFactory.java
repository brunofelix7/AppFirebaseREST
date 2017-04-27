package com.example.appfirebaserest.database;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesFactory {

    private Context context;
    private SharedPreferences pref;
    private SharedPreferences.Editor prefEditor;
    private static final String PREF_NAME = "UserAuth";

    public void saveToken(Context context, String token){
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefEditor = pref.edit();
        prefEditor.putString("token", token);
        prefEditor.apply();
    }

}
