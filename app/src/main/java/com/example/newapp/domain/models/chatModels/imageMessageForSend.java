package com.example.newapp.domain.models.chatModels;

import android.net.Uri;

import java.util.Date;

public class imageMessageForSend extends message{
    public Uri imageURL;

    public imageMessageForSend(String senderName, String senderUID, Date messageSentTime, Uri imageURL) {
        super(senderName, senderUID, messageSentTime);
        this.imageURL = imageURL;
    }
}
