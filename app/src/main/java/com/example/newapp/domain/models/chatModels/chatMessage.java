package com.example.newapp.domain.models.chatModels;

import java.util.Date;

public class chatMessage extends message {
    private String messageStatus;
    public String messageID;

    public chatMessage(String messageID, String senderName, String senderUID, String messageText, Date messageSentTime) {
        super(senderName, senderUID, messageText, messageSentTime);
        this.messageID = messageID;
    }

    public String getMessageStatus() {
        return messageStatus;
    }
    public void setMessageStatus(String messageStatus) {
        this.messageStatus = messageStatus;
    }
}
