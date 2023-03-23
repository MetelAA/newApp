package com.example.newapp.domain.useCases;

import com.example.newapp.domain.models.lessonDescription;
import com.example.newapp.domain.models.repository.getLessonsDescriptionRepository;
import com.example.newapp.global.Response;

import java.util.ArrayList;

public class getLessonsDescriptionUseCase {
    getLessonsDescriptionRepository getLessonsDescriptionRepository;

    public getLessonsDescriptionUseCase(com.example.newapp.domain.models.repository.getLessonsDescriptionRepository getLessonsDescriptionRepository) {
        this.getLessonsDescriptionRepository = getLessonsDescriptionRepository;
    }

    public Response<ArrayList<lessonDescription>, String> getLessonsDescription(){
        return getLessonsDescriptionRepository.getLessonsDescription();
    }
}
