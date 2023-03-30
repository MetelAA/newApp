package com.example.newapp.domain.models.chatModels;

import java.io.Serializable;

public class chatInfo implements Serializable {
    public String chatType, chatID;

    private static final long serialVersionUID = 1L;
    private long unreadMessageCount;
    private messageWithText message;

    public chatInfo(String chatType, String chatID) {
        this.chatType = chatType;
        this.chatID = chatID;
    }

    public chatInfo(String chatID) {
        this.chatID = chatID;
    }

    public void setMessage(messageWithText message) {
        this.message = message;
    }

    public messageWithText getMessage() {
        return message;
    }

    public long getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public void setUnreadMessageCount(long unreadMessageCount) {
        this.unreadMessageCount = unreadMessageCount;
    }

    @Override
    public String toString() {
        return "chatInfo{" +
                "chatType='" + chatType + '\'' +
                ", chatID='" + chatID + '\'' +
                ", unreadMessageCount=" + unreadMessageCount +
                ", message=" + message +
                '}';
    }
}
