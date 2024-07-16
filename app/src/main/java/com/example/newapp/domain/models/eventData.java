package com.example.newapp.domain.models;

import java.io.Serializable;

public class eventData implements Serializable {
    public String eventTitle, eventDescription, eventStartTime, eventEndTime;
    public boolean isFullDay;

    private static final long serialVersionUID = 431L;

    public eventData(String eventTitle, String eventDescription, String eventStartTime, String eventEndTime, boolean isFullDay) {
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
        this.isFullDay = isFullDay;
    }

    public eventData(String eventTitle, String eventDescription, boolean isFullDay) {
        this.eventTitle = eventTitle;
        this.eventDescription = eventDescription;
        this.isFullDay = isFullDay;
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
