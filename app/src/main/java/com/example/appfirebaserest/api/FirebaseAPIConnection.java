package com.example.appfirebaserest.api;

import com.example.appfirebaserest.core.Constants;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Classe que realiza a conexão através do Retrofit, com a minha API do Firebase
 * O Retrofit já possúi um conversor para de objetos para JSON, no caso está sendo usado o GSON como factory
 */
public class FirebaseAPIConnection {

    private static Retrofit retrofit;

    //  RETORNA MINHA CONEXÃO
    public static Retrofit getConnection(){
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }
}
