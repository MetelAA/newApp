package com.example.newapp.domain.models.repository.chatRepository;

import com.example.newapp.domain.models.chatModels.chatInfoWithSnapshotStatus;
import com.example.newapp.global.Response;

import java.util.ArrayList;

public interface getExistingChatsRepository {
    Response<chatInfoWithSnapshotStatus, String> getExistingChats();

}
