package com.example.newapp.domain.useCases;

import com.example.newapp.domain.models.registerAdminData;
import com.example.newapp.domain.models.registerUserData;
import com.example.newapp.domain.models.repository.registerNewUserOrAdminRepository;
import com.example.newapp.global.Response;

public class registerUserUseCase {

    registerNewUserOrAdminRepository register;

    public registerUserUseCase(registerNewUserOrAdminRepository register) {
        this.register = register;
    }

    public Response<String, String> registerNewUser(registerUserData userData){
        Response<String, String> response = register.registerUser(userData);
        return response;
    }
    public Response<String, String> registerNewAdmin(registerAdminData adminData){
        return register.registerAdmin(adminData);
    }
}
