package com.example.newapp.domain.useCases;

import com.example.newapp.domain.models.dayEventsData;
import com.example.newapp.domain.models.repository.getMonthEventsRepository;
import com.example.newapp.global.Response;

import java.time.LocalDate;
import java.util.Map;

public class getMonthEventsUseCase {
    getMonthEventsRepository getMonthEventsRepository;

    public getMonthEventsUseCase(com.example.newapp.domain.models.repository.getMonthEventsRepository getMonthEventsRepository) {
        this.getMonthEventsRepository = getMonthEventsRepository;
    }

    public Response<Map<String, dayEventsData>, String> getMonthEvents(LocalDate date){
        return  getMonthEventsRepository.getMonthEvents(date);
    }
}
