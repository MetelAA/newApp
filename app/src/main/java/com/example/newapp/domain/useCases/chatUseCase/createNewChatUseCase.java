package com.example.newapp.domain.useCases.chatUseCase;

import com.example.newapp.domain.models.chatModels.createNewChatData;
import com.example.newapp.domain.models.repository.chatRepository.createNewChatRepository;
import com.example.newapp.global.Response;

public class createNewChatUseCase {
    createNewChatRepository createNewChatRepository;

    public createNewChatUseCase(com.example.newapp.domain.models.repository.chatRepository.createNewChatRepository createNewChatRepository) {
        this.createNewChatRepository = createNewChatRepository;
    }

    public Response<String, String> createNewChat(createNewChatData chatData){
        return createNewChatRepository.createNewChat(chatData);
    }
}
