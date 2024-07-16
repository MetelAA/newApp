package com.example.newapp.domain.models;

public class lesson {
    public String numberLesson, timeStart, timeEnd, studyRoom, teacher, subject;
    public lesson(String numberLesson, String timeStart, String timeEnd, String studyRoom, String teacher, String subject) {
        this.numberLesson = numberLesson;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.studyRoom = studyRoom;
        this.teacher = teacher;
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "lesson{" +
                "numberLesson='" + numberLesson + '\'' +
                ", timeStart='" + timeStart + '\'' +
                ", timeEnd='" + timeEnd + '\'' +
                ", studyRoom='" + studyRoom + '\'' +
                ", teacher='" + teacher + '\'' +
                ", subject='" + subject + '\'' +
                '}';
    }
}
