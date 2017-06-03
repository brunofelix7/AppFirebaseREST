package com.example.appfirebaserest.api;

import com.example.appfirebaserest.model.Solicitation;
import java.util.HashMap;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Interface que representa minhas URIs e minhas collections no Firebase
 * Através da junção da minha URL_BASE + o método dessa interface, eu posso fazer uma chamada para minha API
 */
public interface FirebaseAPI {

    @GET("ocorrencias.json")
    Call<HashMap<String, Solicitation>> getSolicitations();

}
