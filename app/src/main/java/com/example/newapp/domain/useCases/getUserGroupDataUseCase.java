package com.example.newapp.domain.useCases;

import com.example.newapp.domain.models.profileGroupData;
import com.example.newapp.domain.models.repository.getUserGroupDataRepository;
import com.example.newapp.global.Response;

public class getUserGroupDataUseCase {
    getUserGroupDataRepository profileDataRepository;

    public getUserGroupDataUseCase(getUserGroupDataRepository profileDataRepository) {
        this.profileDataRepository = profileDataRepository;
    }

    public Response<profileGroupData, String> getProfileData(){
        return profileDataRepository.getProfileData();
    }

}
