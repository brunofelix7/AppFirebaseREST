package com.example.appfirebaserest.api;

import com.example.appfirebaserest.model.Message;
import com.example.appfirebaserest.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface FirebaseAPI {

    @GET("message.json")
    Call<Message> getMessages();

}
