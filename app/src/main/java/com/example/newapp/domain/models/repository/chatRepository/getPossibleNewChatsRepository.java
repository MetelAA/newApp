package com.example.newapp.domain.models.repository.chatRepository;

import com.example.newapp.domain.models.chatModels.chatInfoForPossibleNewChat;
import com.example.newapp.global.Response;

import java.util.ArrayList;

public interface getPossibleNewChatsRepository {
    Response<ArrayList<chatInfoForPossibleNewChat>, String> getPossibleNewChats(ArrayList<String> existingChatsMemberUID);
}
