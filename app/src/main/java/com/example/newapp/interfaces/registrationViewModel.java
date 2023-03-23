package com.example.newapp.interfaces;

import com.example.newapp.domain.models.registerAdminData;
import com.example.newapp.domain.models.registerUserData;

public interface registrationViewModel {
    void checkUser();
    void registerNewUser(registerUserData userData);
    void registerAdmin(registerAdminData adminData);

}
