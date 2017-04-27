package com.example.appfirebaserest.api;

import com.example.appfirebaserest.core.Constants;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FirebaseAPIConnection {

    private static Retrofit retrofit;

    public static Retrofit getConnection(){
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

}
