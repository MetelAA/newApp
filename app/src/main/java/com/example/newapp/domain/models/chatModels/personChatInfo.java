package com.example.newapp.domain.models.chatModels;

import java.io.Serializable;

public class personChatInfo extends chatInfo{
    public String comradUID, comradName, comradProfileImage;

    public personChatInfo(String chatType, String chatID, String comradUID, String comradName, String comradProfileImage) {
        super(chatType, chatID);
        this.comradUID = comradUID;
        this.comradName = comradName;
        this.comradProfileImage = comradProfileImage;
    }

    @Override
    public String toString() {
        return "personChatInfo{" +
                "comradUID='" + comradUID + '\'' +
                ", comradName='" + comradName + '\'' +
                ", comradProfileImage='" + comradProfileImage + '\'' +
                ", chatType='" + chatType + '\'' +
                ", chatID='" + chatID + '\'' +
                '}';
    }
}
