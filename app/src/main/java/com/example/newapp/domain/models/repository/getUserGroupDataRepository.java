package com.example.newapp.domain.models.repository;

import com.example.newapp.domain.models.profileGroupData;
import com.example.newapp.global.Response;

public interface getUserGroupDataRepository {
    Response<profileGroupData, String> getProfileData();
}
