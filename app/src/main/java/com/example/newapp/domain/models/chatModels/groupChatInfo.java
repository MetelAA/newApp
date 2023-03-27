package com.example.newapp.domain.models.chatModels;

public class groupChatInfo extends chatInfo{
    public String chatTitle;

    public groupChatInfo(String chatType, String chatID, String chatTitle) {
        super(chatType, chatID);
        this.chatTitle = chatTitle;
    }

    @Override
    public String toString() {
        return "groupChatInfo{" +
                "chatTitle='" + chatTitle + '\'' +
                ", chatType='" + chatType + '\'' +
                ", chatID='" + chatID + '\'' +
                '}';
    }
}
