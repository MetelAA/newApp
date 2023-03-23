package com.example.newapp.domain.useCases;

import com.example.newapp.domain.models.lessonDescription;
import com.example.newapp.domain.models.repository.addDeleteLessonDescriptionRepository;
import com.example.newapp.global.Response;

public class addDeleteLessonDescriptionUseCase {
    addDeleteLessonDescriptionRepository addDeleteLessonDescriptionRepository;

    public addDeleteLessonDescriptionUseCase(addDeleteLessonDescriptionRepository addDeleteLessonDescriptionRepository) {
        this.addDeleteLessonDescriptionRepository = addDeleteLessonDescriptionRepository;
    }

    public Response<lessonDescription, String> addLessonDescription(lessonDescription lessonDescription){
        return addDeleteLessonDescriptionRepository.addLessonDescription(lessonDescription);
    }

    public Response<lessonDescription, String> deleteLessonDescription(lessonDescription lessonDescription){
        return addDeleteLessonDescriptionRepository.deleteLessonDescription(lessonDescription);
    }
}
