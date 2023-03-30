package com.example.newapp.domain.useCases.chatUseCase;

import com.example.newapp.domain.models.chatModels.imageMessageForSend;
import com.example.newapp.domain.models.chatModels.message;
import com.example.newapp.domain.models.chatModels.textMessageForSend;
import com.example.newapp.domain.models.repository.chatRepository.sendMessageRepository;
import com.example.newapp.global.Response;

public class sendMessageUseCase {
    private sendMessageRepository sendMessageRepository;

    public sendMessageUseCase(com.example.newapp.domain.models.repository.chatRepository.sendMessageRepository sendMessageRepository) {
        this.sendMessageRepository = sendMessageRepository;
    }
    public Response<String, String> sendMessage(message message){
        if(message instanceof textMessageForSend){
            return sendMessageRepository.sendTextMessage((textMessageForSend) message);
        }else{
            return sendMessageRepository.sendImageMessage((imageMessageForSend) message);
        }
    }
    public void setChatID(String chatID){
        sendMessageRepository.setChatID(chatID);
    }
}
