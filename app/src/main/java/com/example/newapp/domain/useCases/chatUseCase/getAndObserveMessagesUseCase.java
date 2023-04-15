package com.example.newapp.domain.useCases.chatUseCase;

import com.example.newapp.domain.models.chatModels.chatMessage;
import com.example.newapp.domain.models.chatModels.message;
import com.example.newapp.domain.models.repository.chatRepository.getAndObserveMessagesRepository;
import com.example.newapp.global.Response;

import java.util.ArrayList;
import java.util.Date;

public class getAndObserveMessagesUseCase {
    getAndObserveMessagesRepository getAndObserveMessagesRepository;

    public getAndObserveMessagesUseCase(com.example.newapp.domain.models.repository.chatRepository.getAndObserveMessagesRepository getAndObserveMessagesRepository) {
        this.getAndObserveMessagesRepository = getAndObserveMessagesRepository;
    }
    public Response<ArrayList<message>, String> getPreviousMessages(Date startSearchDate){
        return getAndObserveMessagesRepository.getPreviousMessages(startSearchDate);
    }
    public Response<message, String> getNewMessages(Date startSearchDate){
        return getAndObserveMessagesRepository.getNewMessages(startSearchDate);
    }

    public void removeListeners(){
        getAndObserveMessagesRepository.removeListener();
    }
    public void setChatID(String chatID){
        getAndObserveMessagesRepository.setChatID(chatID);
    }
}
