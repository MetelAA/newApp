package com.example.newapp.domain.models.repository;

import android.net.Uri;

import com.example.newapp.global.Response;

public interface setUserProfileImageRepository {
    public Response<String, String> setUserProfileImage(Uri imageUri);
}
