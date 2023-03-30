package com.example.newapp.domain.models.chatModels;

import java.util.Date;

public class chatMessageWithImage extends message {
    public String messageID, messageImageURL;
    private String messageStatus;


    public chatMessageWithImage(String messageID, String senderName, String senderUID, String messageImageURL, Date messageSentTime) {
        super(senderName, senderUID, messageSentTime);
        this.messageID = messageID;
        this.messageImageURL = messageImageURL;
    }

    public String getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(String messageStatus) {
        this.messageStatus = messageStatus;
    }
}
