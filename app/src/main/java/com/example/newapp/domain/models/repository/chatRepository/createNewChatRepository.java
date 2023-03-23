package com.example.newapp.domain.models.repository.chatRepository;

import com.example.newapp.domain.models.chatModels.createNewChatData;
import com.example.newapp.global.Response;

import java.util.ResourceBundle;

public interface createNewChatRepository {
    Response<String, String> createNewChat(createNewChatData newChatData);
}
