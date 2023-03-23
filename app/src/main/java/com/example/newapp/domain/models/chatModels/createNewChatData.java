package com.example.newapp.domain.models.chatModels;

import java.util.ArrayList;
import java.util.List;

public class createNewChatData {
    public List<String> chatMembersUID, chatMembersNames;
    public String chatType;
    public message message;

    public createNewChatData(List<String> chatMembersUID, List<String> chatMembersNames, String chatType, com.example.newapp.domain.models.chatModels.message message) {
        this.chatMembersUID = chatMembersUID;
        this.chatMembersNames = chatMembersNames;
        this.chatType = chatType;
        this.message = message;
    }
}
