package com.example.newapp.domain.models.repository.chatRepository;

import com.example.newapp.domain.models.chatModels.chatMessage;
import com.example.newapp.domain.models.chatModels.message;
import com.example.newapp.global.Response;

import java.util.ArrayList;
import java.util.Date;

public interface getAndObserveMessagesRepository {
    Response<ArrayList<message>, String> getPreviousMessages(Date startSearchDate);
    Response<message, String> getNewMessages(Date startSearchDate);
    void setChatID(String chatID);
    void removeListener();
}
