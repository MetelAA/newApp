package com.example.newapp.domain.models.repository.chatRepository;

import com.example.newapp.domain.models.chatModels.userStatus;
import com.example.newapp.global.Response;

public interface getAndObserveUserStatusRepository {
    Response<userStatus, String> getComradStatus(String comradUID);
    void removeListener();
}
