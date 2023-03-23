package com.example.newapp.domain.useCases.chatUseCase;

import com.example.newapp.domain.models.repository.chatRepository.getAndObserveUsersStatusEsRepository;
import com.example.newapp.domain.models.chatModels.userStatusWithChangeType;
import com.example.newapp.global.Response;

import java.util.ArrayList;

public class getAndObserveUsersStatusEsUseCase {
    getAndObserveUsersStatusEsRepository getAndObserveUsersStatusEsRepository;

    public getAndObserveUsersStatusEsUseCase(getAndObserveUsersStatusEsRepository getAndObserveUsersStatusEsRepository) {
        this.getAndObserveUsersStatusEsRepository = getAndObserveUsersStatusEsRepository;
    }

    public Response<ArrayList<userStatusWithChangeType>, String> getUsersStatuses(ArrayList<String> usersUIDs){
        return getAndObserveUsersStatusEsRepository.getUsersStatusEs(usersUIDs);
    }
}
