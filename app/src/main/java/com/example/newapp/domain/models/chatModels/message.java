package com.example.newapp.domain.models.chatModels;

import java.util.Date;

public class message {
    public String senderName, senderUID;
    public Date messageSentTime;

    public message(String senderName, String senderUID, Date messageSentTime) {
        this.senderName = senderName;
        this.senderUID = senderUID;
        this.messageSentTime = messageSentTime;
    }

    @Override
    public String toString() {
        return "message{" +
                "senderName='" + senderName + '\'' +
                ", senderUID='" + senderUID + '\'' +
                ", messageSentTime=" + messageSentTime +
                '}';
    }
}
