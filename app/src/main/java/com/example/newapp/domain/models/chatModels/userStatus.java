package com.example.newapp.domain.models.chatModels;

import java.util.Date;

public class userStatus {
    public String userUID, userStatus;
    private Date lastTimeSeen;

    public userStatus(String userUID, String userStatus) {
        this.userUID = userUID;
        this.userStatus = userStatus;
    }

    public Date getLastTimeSeen() {
        return lastTimeSeen;
    }

    public void setLastTimeSeen(Date lastTimeSeen) {
        this.lastTimeSeen = lastTimeSeen;
    }
}

