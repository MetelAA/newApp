package com.example.newapp.domain.models.chatModels;


public class chatInfo {
    public String chatType, chatID;

    private long unreadMessageCount;
    private message message;

    public chatInfo(String chatType, String chatID) {
        this.chatType = chatType;
        this.chatID = chatID;
    }

    public void setMessage(message message) {
        this.message = message;
    }

    public message getMessage() {
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
