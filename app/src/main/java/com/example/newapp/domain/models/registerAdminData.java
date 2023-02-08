package com.example.newapp.domain.models;

public class registerAdminData extends registerUserData{
    public String groupName;

    public registerAdminData(String email, String name, String password, String type, String groupName) {
        super(email, name, password, type);
        this.groupName = groupName;
    }
}
