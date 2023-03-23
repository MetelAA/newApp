package com.example.newapp.domain.models.repository;

import com.example.newapp.domain.models.lesson;
import com.example.newapp.global.Response;

import java.util.ArrayList;

public interface addDayLessonsRepository {
    Response<String, String> addDayLessons(ArrayList<lesson> listLessons, String dayOfWeek);
}
