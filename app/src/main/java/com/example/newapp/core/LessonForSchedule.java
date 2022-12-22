package com.example.newapp.core;

public class LessonForSchedule {
    public String numberLesson, timeStart, timeEnd, studyRoom, teacher, subject;

    public LessonForSchedule(String numberLesson, String timeStart, String timeEnd, String studyRoom, String teacher, String subject) {
        this.numberLesson = numberLesson;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.studyRoom = studyRoom;
        this.teacher = teacher;
        this.subject = subject;
    }
}
