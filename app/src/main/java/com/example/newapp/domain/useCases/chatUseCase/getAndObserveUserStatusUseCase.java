package com.example.newapp.domain.useCases.chatUseCase;

import com.example.newapp.domain.models.chatModels.userStatus;
import com.example.newapp.domain.models.repository.chatRepository.getAndObserveUserStatusRepository;
import com.example.newapp.global.Response;

public class getAndObserveUserStatusUseCase {
    getAndObserveUserStatusRepository getAndObserveUserStatusRepository;

    public getAndObserveUserStatusUseCase(getAndObserveUserStatusRepository getAndObserveUserStatusRepository) {
        this.getAndObserveUserStatusRepository = getAndObserveUserStatusRepository;
    }

    public Response<userStatus, String> getComradStatus(String comradUID){
        return getAndObserveUserStatusRepository.getComradStatus(comradUID);
    }
    public void removeListeners(){
        getAndObserveUserStatusRepository.removeListener();
    }
}
