package com.example.newapp.domain.useCases;

import android.net.Uri;

import com.example.newapp.domain.models.repository.setUserProfileImageRepository;
import com.example.newapp.global.Response;

public class setUserProfileImageUseCase {
    setUserProfileImageRepository setUserProfileImageRepository;

    public setUserProfileImageUseCase(com.example.newapp.domain.models.repository.setUserProfileImageRepository setUserProfileImageRepository) {
        this.setUserProfileImageRepository = setUserProfileImageRepository;
    }
    public Response<String, String> setUserImageProfile(Uri imageUri){
        return setUserProfileImageRepository.setUserProfileImage(imageUri);
    }
}
