package com.example.newapp.domain.models;

public class eventData {
    public String eventTitle, eventDescription, eventStartTime, eventEndTime;

    public eventData(String eventTitle, String eventDescription, String eventStartTime, String eventEndTime) {
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
    }

    @Override
    public String toString() {
        return "eventData{" +
                "eventTitle='" + eventTitle + '\'' +
                ", eventDescription='" + eventDescription + '\'' +
                ", eventStartTime='" + eventStartTime + '\'' +
                ", eventEndTime='" + eventEndTime + '\'' +
                '}';
    }
}
