package com.example.newapp.domain.models;

import java.util.ArrayList;

public class dayEventsData {
    public ArrayList<eventData> events;
    public String dayDate;

    public dayEventsData(ArrayList<eventData> events, String dayDate) {
        this.events = events;
        this.dayDate = dayDate;
    }

    @Override
    public String toString() {
        return "dayEventsData{" +
                "events=" + events +
                ", dayDate='" + dayDate + '\'' +
                '}';
    }
}
