package com.example.newapp.interfaces;

import com.example.newapp.domain.models.lessonDescription;

public interface changeLessonsForScheduleViewModel {
    void getLessonsDescription();
    void addLessonDescription(lessonDescription lessonDescription);
    void deleteLessonDescription(lessonDescription lessonDescription);
}
