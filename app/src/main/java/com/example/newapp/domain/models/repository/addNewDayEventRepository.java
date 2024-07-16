package com.example.newapp.domain.models.repository;

import com.example.newapp.domain.models.eventData;
import com.example.newapp.global.Response;

import java.time.LocalDate;

public interface addNewDayEventRepository {
    Response<eventData, String> addNewDauEvent(eventData data, LocalDate date);
}
