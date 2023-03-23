package com.example.newapp.domain.models.repository;

import com.example.newapp.domain.models.lessonDescription;
import com.example.newapp.global.Response;

import java.util.ArrayList;

public interface getLessonsDescriptionRepository {
    Response<ArrayList<lessonDescription>, String> getLessonsDescription();
}
