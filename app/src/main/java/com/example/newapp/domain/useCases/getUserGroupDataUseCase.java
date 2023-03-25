package com.example.newapp.domain.useCases;

import com.example.newapp.domain.models.profileDataAboutGroup;
import com.example.newapp.domain.models.repository.getProfileDataRepositry;
import com.example.newapp.global.Response;

public class getUserGroupDataUseCase {
    getProfileDataRepositry profileDataRepository;

    public getUserGroupDataUseCase(getProfileDataRepositry profileDataRepository) {
        this.profileDataRepository = profileDataRepository;
    }

    public Response<profileDataAboutGroup, String> getProfileData(){
        return profileDataRepository.getProfileData();
    }

}
