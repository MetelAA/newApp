package com.example.newapp.domain.useCases;

import com.example.newapp.domain.models.groupUserData;
import com.example.newapp.domain.models.repository.getDeleteGroupUsersRepository;
import com.example.newapp.global.Response;

import java.util.ArrayList;

public class getListKickGroupUsersUseCase {

    getDeleteGroupUsersRepository getDeleteGroupUsersRepository;

    public getListKickGroupUsersUseCase(com.example.newapp.domain.models.repository.getDeleteGroupUsersRepository getDeleteGroupUsersRepository) {
        this.getDeleteGroupUsersRepository = getDeleteGroupUsersRepository;
    }

    public Response<ArrayList<groupUserData>, String> getListGroupUsers(){
        return getDeleteGroupUsersRepository.getUsersList();
    }
    public Response<String, String> deleteUser(String deleteUserUID){
        return getDeleteGroupUsersRepository.deleteUser(deleteUserUID);
    }
}
