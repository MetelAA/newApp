package com.example.newapp.interfaces;

import com.example.newapp.domain.models.eventData;

import java.time.LocalDate;

public interface newsDialogViewModel {
    void setNewDayEvent(eventData event, LocalDate date);
}
