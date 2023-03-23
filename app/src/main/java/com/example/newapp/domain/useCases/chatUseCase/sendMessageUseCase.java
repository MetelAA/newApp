package com.example.newapp.domain.useCases.chatUseCase;

import android.speech.RecognitionService;

import com.example.newapp.domain.models.chatModels.message;
import com.example.newapp.domain.models.repository.chatRepository.sendMessageRepository;
import com.example.newapp.global.Response;

public class sendMessageUseCase {
    sendMessageRepository sendMessageRepository;

    public sendMessageUseCase(com.example.newapp.domain.models.repository.chatRepository.sendMessageRepository sendMessageRepository) {
        this.sendMessageRepository = sendMessageRepository;
    }
    public Response<String, String> sendMessage(message message){
        return sendMessageRepository.sendMessage(message);
    }
    public void setChatID(String chatID){
        sendMessageRepository.setChatID(chatID);
    }
}
