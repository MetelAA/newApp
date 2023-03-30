package com.example.newapp.domain.models.repository.chatRepository;

import com.example.newapp.domain.models.chatModels.imageMessageForSend;
import com.example.newapp.domain.models.chatModels.textMessageForSend;
import com.example.newapp.global.Response;

public interface sendMessageRepository {
    Response<String, String> sendTextMessage(textMessageForSend message);
    Response<String, String> sendImageMessage(imageMessageForSend message);
    void setChatID(String chatID);
}
