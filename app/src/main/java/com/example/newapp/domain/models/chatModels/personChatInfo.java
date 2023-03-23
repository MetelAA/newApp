package com.example.newapp.domain.models.chatModels;

public class personChatInfo extends chatInfo{
    public String comradName, comradUID;

    public personChatInfo(String chatType, String chatID, String comradName, String comradUID) {
        super(chatType, chatID);
        this.comradName = comradName;
        this.comradUID = comradUID;
    }


}
