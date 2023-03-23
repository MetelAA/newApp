package com.example.newapp.domain.models.repository;

import com.example.newapp.domain.models.lessonDescription;
import com.example.newapp.global.Response;

public interface addDeleteLessonDescriptionRepository {
    Response<lessonDescription, String> addLessonDescription(lessonDescription lessonDescription);
    Response<lessonDescription, String> deleteLessonDescription(lessonDescription lessonDescription);
}
