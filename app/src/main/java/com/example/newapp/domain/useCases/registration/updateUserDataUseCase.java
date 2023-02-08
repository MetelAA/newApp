package com.example.newapp.domain.useCases.registration;

import com.example.newapp.domain.models.repository.updateUserDataRepository;

public class updateUserDataUseCase {
    updateUserDataRepository repository;

    public updateUserDataUseCase(updateUserDataRepository repository) {
        this.repository = repository;
    }

    public void checkUser(){
        repository.checkUser();
    }
}
