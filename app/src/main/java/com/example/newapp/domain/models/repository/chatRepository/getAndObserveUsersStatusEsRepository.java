package com.example.newapp.domain.models.repository.chatRepository;

import com.example.newapp.domain.models.chatModels.userStatusWithChangeType;
import com.example.newapp.global.Response;

import java.util.ArrayList;

public interface getAndObserveUsersStatusEsRepository {
    Response<ArrayList<userStatusWithChangeType>, String> getUsersStatusEs(ArrayList<String> userUIDs);
}
