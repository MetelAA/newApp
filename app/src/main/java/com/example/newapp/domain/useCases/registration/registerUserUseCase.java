package com.example.newapp.domain.useCases.registration;

import android.util.Log;

import com.example.newapp.domain.models.registerAdminData;
import com.example.newapp.domain.models.registerUserData;
import com.example.newapp.domain.models.repository.registerNewUserOrAdminRepository;

public class registerUserUseCase {

    registerNewUserOrAdminRepository register;

    public registerUserUseCase(registerNewUserOrAdminRepository register) {
        this.register = register;
    }

    public void registerNewUser(registerUserData userData){
        register.registerUser(userData);
    }
    public void registerNewAdmin(registerAdminData adminData){
        register.registerAdmin(adminData);
    }
}
