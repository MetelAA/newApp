package com.example.newapp.core;

import java.util.Date;

public class textMessagePersonChat {
    public String message, senderUID;
    public Date sentTime;

    public textMessagePersonChat(String message, String senderUID, Date sentTime) {
        this.message = message;
        this.senderUID = senderUID;
        this.sentTime = sentTime;
    }
}
