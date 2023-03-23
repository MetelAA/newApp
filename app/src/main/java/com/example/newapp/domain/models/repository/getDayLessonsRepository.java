package com.example.newapp.domain.models.repository;

import com.example.newapp.domain.models.arrayListForSchedule;
import com.example.newapp.global.Response;

public interface getDayLessonsRepository {
    Response<arrayListForSchedule, String> getDayLessons(String dayOfWeek);
}
