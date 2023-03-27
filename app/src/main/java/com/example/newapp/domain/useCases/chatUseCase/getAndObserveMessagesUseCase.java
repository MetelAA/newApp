package com.example.newapp.domain.useCases.chatUseCase;

import com.example.newapp.domain.models.chatModels.chatMessage;
import com.example.newapp.domain.models.repository.chatRepository.getAndObserveMessagesRepository;
import com.example.newapp.global.Response;

import java.util.ArrayList;
import java.util.Date;

public class getAndObserveMessagesUseCase {
    getAndObserveMessagesRepository getAndObserveMessagesRepository;

    public getAndObserveMessagesUseCase(com.example.newapp.domain.models.repository.chatRepository.getAndObserveMessagesRepository getAndObserveMessagesRepository) {
        this.getAndObserveMessagesRepository = getAndObserveMessagesRepository;
    }
    public Response<ArrayList<chatMessage>, String> getPreviousMessages(Date startSearchDate, String chatUID){
        return getAndObserveMessagesRepository.getPreviousMessages(startSearchDate, chatUID);
    }
    public Response<ArrayList<chatMessage>, String> getNewMessages(Date startSearchDate, String chatUID){
        return getAndObserveMessagesRepository.getNewMessages(startSearchDate, chatUID);
    }
}
