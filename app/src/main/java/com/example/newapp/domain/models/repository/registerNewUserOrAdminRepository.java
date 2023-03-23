package com.example.newapp.domain.models.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.newapp.domain.models.registerAdminData;
import com.example.newapp.domain.models.registerUserData;
import com.example.newapp.global.Response;

public interface registerNewUserOrAdminRepository {
    public Response<String, String> registerUser(registerUserData userData);
    public Response<String, String> registerAdmin(registerAdminData adminData);

}
