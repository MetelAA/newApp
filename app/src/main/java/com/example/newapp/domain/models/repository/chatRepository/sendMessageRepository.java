package com.example.newapp.domain.models.repository.chatRepository;

import com.example.newapp.domain.models.chatModels.message;
import com.example.newapp.global.Response;

public interface sendMessageRepository {
    Response<String, String> sendMessage(message message);
    void setChatID(String chatID);
}
