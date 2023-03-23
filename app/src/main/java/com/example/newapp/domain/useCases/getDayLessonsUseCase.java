package com.example.newapp.domain.useCases;

import com.example.newapp.domain.models.arrayListForSchedule;
import com.example.newapp.domain.models.repository.getDayLessonsRepository;
import com.example.newapp.global.Response;

public class getDayLessonsUseCase {
    getDayLessonsRepository getDayLessonsRepository;
    public getDayLessonsUseCase(com.example.newapp.domain.models.repository.getDayLessonsRepository getDayLessonsRepository) {
        this.getDayLessonsRepository = getDayLessonsRepository;
    }

    public Response<arrayListForSchedule, String> getDayLessons(String dayOfWeek){
        return getDayLessonsRepository.getDayLessons(dayOfWeek);
    }
}
