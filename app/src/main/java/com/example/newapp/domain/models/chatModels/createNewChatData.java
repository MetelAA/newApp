package com.example.newapp.domain.models.chatModels;

import java.util.ArrayList;
import java.util.List;

public class createNewChatData {
    public List<String> chatMembersUID;
    public String chatType;

    public createNewChatData( String chatType, List<String> chatMembersUID) {
        this.chatMembersUID = chatMembersUID;
        this.chatType = chatType;
    }
}
