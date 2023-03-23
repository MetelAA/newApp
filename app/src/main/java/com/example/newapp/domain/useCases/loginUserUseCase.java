package com.example.newapp.domain.useCases;

import com.example.newapp.domain.models.loginUserData;
import com.example.newapp.domain.models.repository.loginUserRepository;
import com.example.newapp.global.Response;

public class loginUserUseCase {
    loginUserRepository loginUserRepository;

    public loginUserUseCase(com.example.newapp.domain.models.repository.loginUserRepository loginUserRepository) {
        this.loginUserRepository = loginUserRepository;
    }

    public Response<String, String> loginUser(loginUserData data){
        return loginUserRepository.loginUser(data);
    }
}
