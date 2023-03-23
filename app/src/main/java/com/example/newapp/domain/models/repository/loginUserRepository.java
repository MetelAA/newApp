package com.example.newapp.domain.models.repository;

import com.example.newapp.domain.models.loginUserData;
import com.example.newapp.global.Response;

public interface loginUserRepository {
    Response<String, String> loginUser(loginUserData data);
}
