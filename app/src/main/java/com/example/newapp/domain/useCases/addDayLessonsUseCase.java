package com.example.newapp.domain.useCases;

import com.example.newapp.domain.models.lesson;
import com.example.newapp.domain.models.repository.addDayLessonsRepository;
import com.example.newapp.global.Response;

import java.util.ArrayList;

public class addDayLessonsUseCase {
    addDayLessonsRepository addDayLessonsRepository;

    public addDayLessonsUseCase(com.example.newapp.domain.models.repository.addDayLessonsRepository addDayLessonsRepository) {
        this.addDayLessonsRepository = addDayLessonsRepository;
    }

    Response<String, String> addDayLessons(ArrayList<lesson> listLessons, String dayOfWeek){
        return addDayLessonsRepository.addDayLessons(listLessons, dayOfWeek);
    }
}
