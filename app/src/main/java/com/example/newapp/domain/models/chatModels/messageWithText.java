package com.example.newapp.domain.models.chatModels;

import java.io.Serializable;
import java.util.Date;

public class messageWithText extends message implements Serializable {
    public String messageText;
    private static final long serialVersionUID = 2L;

    public messageWithText(String senderName, String senderUID, String messageText, Date messageSentTime) {
        super(senderName, senderUID, messageSentTime);
        this.messageText = messageText;
    }
}
