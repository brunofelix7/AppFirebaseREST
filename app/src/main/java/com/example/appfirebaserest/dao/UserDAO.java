package com.example.appfirebaserest.dao;

import com.example.appfirebaserest.model.User;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

public class UserDAO extends SugarRecord<User>{

    private String uid;
    private String email;

    @Ignore
    private User user;

    public UserDAO(){

    }

    public UserDAO(String email, String uid) {
        this.email = email;
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
