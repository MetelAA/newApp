package com.example.newapp.domain.useCases;

import com.example.newapp.domain.models.repository.updateUserDataRepository;
import com.example.newapp.global.Response;

public class updateUserDataUseCase {
    updateUserDataRepository repository;

    public updateUserDataUseCase(updateUserDataRepository repository) {
        this.repository = repository;
    }

    public Response<String, String> checkUser(){
        return repository.checkUser();
    }
}
