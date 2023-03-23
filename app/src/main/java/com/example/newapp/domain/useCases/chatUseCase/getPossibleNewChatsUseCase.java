package com.example.newapp.domain.useCases.chatUseCase;

import com.example.newapp.domain.models.chatModels.chatInfoForNewChat;
import com.example.newapp.domain.models.repository.chatRepository.getPossibleNewChatsRepository;
import com.example.newapp.global.Response;

import java.util.ArrayList;

public class getPossibleNewChatsUseCase {
    getPossibleNewChatsRepository getPossibleNewChatsRepository;

    public getPossibleNewChatsUseCase(getPossibleNewChatsRepository getPossibleNewChatsRepository) {
        this.getPossibleNewChatsRepository = getPossibleNewChatsRepository;
    }

    public Response<ArrayList<chatInfoForNewChat>, String> getPossibleNewChats(ArrayList<String> existingChatsMemberUID){
        return getPossibleNewChatsRepository.getPossibleNewChats(existingChatsMemberUID);
    }
}