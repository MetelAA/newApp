package com.example.newapp.domain.models.repository.chatRepository;

import com.example.newapp.domain.models.chatModels.chatInfoForNewChat;
import com.example.newapp.global.Response;

import java.util.ArrayList;

public interface getPossibleNewChatsRepository {
    Response<ArrayList<chatInfoForNewChat>, String> getPossibleNewChats(ArrayList<String> existingChatsMemberUID);
}
