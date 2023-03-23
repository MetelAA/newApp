package com.example.newapp.domain.useCases.chatUseCase;

import com.example.newapp.domain.models.chatModels.chatInfoWithSnapshotStatus;
import com.example.newapp.domain.models.repository.chatRepository.getExistingChatsRepository;
import com.example.newapp.global.Response;

import java.util.ArrayList;

public class getExistingChatsUseCase {
    getExistingChatsRepository getExistingChatsRepository;

    public getExistingChatsUseCase(com.example.newapp.domain.models.repository.chatRepository.getExistingChatsRepository getExistingChatsRepository) {
        this.getExistingChatsRepository = getExistingChatsRepository;
    }
    public Response<ArrayList<chatInfoWithSnapshotStatus>, String> getExistingChat(){
        return getExistingChatsRepository.getExistingChats();
    }
}
