package com.example.newapp.domain.models;

public class registerUserData {
    public String email, name, password, type;

    public registerUserData(String email, String name, String password, String type) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.type = type;
    }
}
