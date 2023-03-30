package com.example.newapp.domain.models.chatModels;

import java.util.Date;

public class chatMessageWithText extends message {
    public String messageText, messageID;
    private String messageStatus;

    public chatMessageWithText(String messageID, String senderName, String senderUID, String messageText, Date messageSentTime) {
        super(senderName, senderUID, messageSentTime);
        this.messageText = messageText;
        this.messageID = messageID;
    }

    public String getMessageStatus() {
        return messageStatus;
    }
    public void setMessageStatus(String messageStatus) {
        this.messageStatus = messageStatus;
    }
}
