package com.example.newapp.domain.models.repository;


import androidx.lifecycle.MutableLiveData;

import com.example.newapp.global.Response;

public interface updateUserDataRepository {
    Response<String, String> checkUser();
}
