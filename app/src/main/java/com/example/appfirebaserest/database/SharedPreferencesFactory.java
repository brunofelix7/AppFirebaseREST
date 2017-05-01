package com.example.appfirebaserest.database;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.appfirebaserest.core.Constants;
import static android.content.Context.MODE_PRIVATE;

/**
 * Classe usada para salvar e pegar o token nas preferências do usuário
 */
public class SharedPreferencesFactory {

    private String token;
    private SharedPreferences pref;
    private SharedPreferences.Editor prefEditor;

    //  SALVA O TOKEN NAS PREFERÊNCIAS DO USUÁRIO COM A CHAVE token
    public void saveToken(Context context, String token){
        pref = context.getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        prefEditor = pref.edit();
        prefEditor.putString("token", token);
        prefEditor.apply();
    }

    //  ACESSA AS PREFERÊNCIAS DO USUÁRIO, BUSCA E RETORNA UM TOKEN PELA CHAVE token
    public String getToken(Context context){
        pref = context.getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        token = pref.getString("token", null);
        return token;
    }

    //  LIMPA AS PREFERÊNCIAS DO USUÁRIO
    public void deletePreferences(Context context){
        pref = context.getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        prefEditor = pref.edit();
        prefEditor.clear();
        prefEditor.apply();
    }

}
