package com.example.newapp.domain.models.repository;

import com.example.newapp.domain.models.dayEventsData;
import com.example.newapp.global.Response;

import java.time.LocalDate;
import java.util.Map;

public interface getMonthEventsRepository {
    Response<Map<String, dayEventsData>, String> getMonthEvents(LocalDate date);
}
