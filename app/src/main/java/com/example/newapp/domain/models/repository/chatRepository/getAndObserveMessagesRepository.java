package com.example.newapp.domain.models.repository.chatRepository;

import com.example.newapp.domain.models.chatModels.chatMessage;
import com.example.newapp.global.Response;

import java.util.ArrayList;
import java.util.Date;

public interface getAndObserveMessagesRepository {
    Response<ArrayList<chatMessage>, String> getPreviousMessages(Date startSearchDate);
    Response<ArrayList<chatMessage>, String> getNewMessages(Date startObserveDate);
}
