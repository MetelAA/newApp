package com.example.newapp.domain.useCases;

import com.example.newapp.domain.models.eventData;
import com.example.newapp.domain.models.repository.addNewDayEventRepository;
import com.example.newapp.global.Response;

import java.time.LocalDate;

public class addNewDayEventUseCase {
    private addNewDayEventRepository addNewDayEventRepository;

    public addNewDayEventUseCase(com.example.newapp.domain.models.repository.addNewDayEventRepository addNewDayEventRepository) {
        this.addNewDayEventRepository = addNewDayEventRepository;
    }

    public Response<eventData, String> addNewEvent(eventData eventData, LocalDate date){
        return addNewDayEventRepository.addNewDauEvent(eventData, date);
    }
}
