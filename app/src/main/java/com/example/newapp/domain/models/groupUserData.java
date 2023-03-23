package com.example.newapp.domain.models;

public class groupUserData {
    public String UserUID ,userName, userEmail, userType;

    public groupUserData(String userUID, String userName, String userEmail, String userType) {
        this.UserUID = userUID;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userType = userType;
    }
}
