package com.example.newapp.interfaces;

import java.util.Date;

public interface chatViewModel {
    void getComradStatus(String comradUID);
    void getNewMessages(String chatID, Date startSearchDate);
    void getPreviousMessages(String chatID, Date startSearchDate);
    void sendMessage();
    void createNewChat();
    void removeListeners();
}
