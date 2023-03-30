package com.example.newapp.domain.models.chatModels;

import java.util.Date;

public class textMessageForSend extends message{
    public String messageText;

    public textMessageForSend(String senderName, String senderUID, Date messageSentTime, String messageText) {
        super(senderName, senderUID, messageSentTime);
        this.messageText = messageText;
    }
}
