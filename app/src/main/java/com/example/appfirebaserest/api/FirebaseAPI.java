package com.example.appfirebaserest.api;

import com.example.appfirebaserest.model.Solicitation;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.GET;

public interface FirebaseAPI {

    @GET("ocorrencias.json")
    Call<HashMap<String, Solicitation>> getSolicitations();

}
