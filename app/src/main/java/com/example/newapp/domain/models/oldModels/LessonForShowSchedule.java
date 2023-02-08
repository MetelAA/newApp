package com.example.newapp.domain.models.oldModels;

public class LessonForShowSchedule {
    public String numberLesson, timeStart, timeEnd, studyRoom, teacher, subject;

    public LessonForShowSchedule(String numberLesson, String timeStart, String timeEnd, String studyRoom, String teacher, String subject) {
        this.numberLesson = numberLesson;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.studyRoom = studyRoom;
        this.teacher = teacher;
        this.subject = subject;
    }
}
