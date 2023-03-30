package com.example.newapp.interfaces;

import com.example.newapp.domain.models.chatModels.createNewChatData;
import com.example.newapp.domain.models.chatModels.message;

import java.util.Date;

public interface chatViewModel {
    void getAndObserveComradStatus(String comradUID);
    void getNewMessages(Date startSearchDate);
    void getPreviousMessages(Date startSearchDate);
    void sendMessage(message message);
    void setChatID(String chatID);
    void createNewChat(createNewChatData chatData);
    void removeListeners();
}
