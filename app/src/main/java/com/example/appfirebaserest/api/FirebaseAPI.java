package com.example.appfirebaserest.api;

import com.example.appfirebaserest.model.Solicitation;
import java.util.HashMap;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Interface que representa a minha collection "ocorrencias" no Firebase
 * Através da junção da minha URI + o método dessa interface, eu posso fazer uma chamada GET para minha API
 */
public interface FirebaseAPI {

    @GET("ocorrencias.json")
    Call<HashMap<String, Solicitation>> getSolicitations();

}
