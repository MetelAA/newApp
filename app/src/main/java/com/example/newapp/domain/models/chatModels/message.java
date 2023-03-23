package com.example.newapp.domain.models.chatModels;

import java.util.Date;

public class message {
    public String senderName, senderUID, messageText;
    public Date messageSentTime;

    public message(String senderName, String senderUID, String messageText, Date messageSentTime) {
        this.senderName = senderName;
        this.senderUID = senderUID;
        this.messageText = messageText;
        this.messageSentTime = messageSentTime;
    }

    @Override
    public String toString() {
        return "message{" +
                "senderName='" + senderName + '\'' +
                ", senderUID='" + senderUID + '\'' +
                ", messageText='" + messageText + '\'' +
                ", messageSentTime=" + messageSentTime +
                '}';
    }
}
