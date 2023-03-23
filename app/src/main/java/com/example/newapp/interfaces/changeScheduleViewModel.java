package com.example.newapp.interfaces;

import com.example.newapp.domain.models.lesson;

import java.util.ArrayList;

public interface changeScheduleViewModel {
    void getLessonsDescription();
    void getDayLessons(String dayOfWeek);
    void addDayLessons(ArrayList<lesson> listLessons, String dayOfWeek);
}
