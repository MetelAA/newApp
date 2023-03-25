package com.example.newapp.domain.models.repository;

import com.example.newapp.domain.models.profileDataAboutGroup;
import com.example.newapp.global.Response;

public interface getProfileDataRepositry {
    Response<profileDataAboutGroup, String> getProfileData();
}
