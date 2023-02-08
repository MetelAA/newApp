package com.example.newapp.domain.models.repository;

import com.example.newapp.domain.models.registerAdminData;
import com.example.newapp.domain.models.registerUserData;

public interface registerNewUserOrAdminRepository {
    public void registerUser(registerUserData userData);
    public void registerAdmin(registerAdminData adminData);

}
