package com.example.newapp.ui.schedule.supportScreens;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.newapp.data.repository.addDayLessonsRepositoryImpl;
import com.example.newapp.data.repository.getDayLessonsRepositoryImpl;
import com.example.newapp.data.repository.getLessonsDescriptionRepositoryImpl;
import com.example.newapp.domain.models.arrayListForSchedule;
import com.example.newapp.domain.models.lesson;
import com.example.newapp.domain.models.lessonDescription;
import com.example.newapp.domain.useCases.addDayLessonsUseCase;
import com.example.newapp.domain.useCases.getDayLessonsUseCase;
import com.example.newapp.domain.useCases.getLessonsDescriptionUseCase;
import com.example.newapp.global.Response;
import com.example.newapp.interfaces.changeScheduleViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class changeScheduleViewModelImpl extends ViewModel implements changeScheduleViewModel {
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    private getDayLessonsRepositoryImpl getDayLessonsRepository = new getDayLessonsRepositoryImpl(fStore);
    private getDayLessonsUseCase getDayLessonsUseCase = new getDayLessonsUseCase(getDayLessonsRepository);
    private getLessonsDescriptionRepositoryImpl getLessonsDescriptionRepository = new getLessonsDescriptionRepositoryImpl(fStore);
    private getLessonsDescriptionUseCase getLessonsDescriptionUseCase = new getLessonsDescriptionUseCase(getLessonsDescriptionRepository);
    private addDayLessonsRepositoryImpl addDayLessonsRepository = new addDayLessonsRepositoryImpl(fStore);
    private addDayLessonsUseCase addDayLessonsUseCase = new addDayLessonsUseCase(addDayLessonsRepository);

    MutableLiveData<String> onErrorLiveData = new MutableLiveData<>();
    MutableLiveData<String> onDayLessonsAddedLiveData = new MutableLiveData<>();
    MutableLiveData<ArrayList<lessonDescription>> onGotLessonsDescription = new MutableLiveData<>();
    MutableLiveData<arrayListForSchedule> onGotDayLessonsLiveData = new MutableLiveData<>();


    @Override
    public void getLessonsDescription() {
        Response<ArrayList<lessonDescription>, String> response = getLessonsDescriptionUseCase.getLessonsDescription();
        response.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                if(response.getError() == null){
                    onGotLessonsDescription.postValue(response.getData());
                }else{
                    onErrorLiveData.postValue(response.getError());
                }
                response.deleteObservers();
            }
        });
    }

    @Override
    public void getDayLessons(String dayOfWeek) {
        Response<arrayListForSchedule, String> response = getDayLessonsUseCase.getDayLessons(dayOfWeek);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                if(response.getError() == null){
                    onGotDayLessonsLiveData.postValue(response.getData());
                }else{
                    onErrorLiveData.postValue(response.getError());
                }
                response.deleteObservers();
            }
        });
    }

    @Override
    public void addDayLessons(ArrayList<lesson> listLessons, String dayOfWeek) {
        Response<String, String> response = addDayLessonsRepository.addDayLessons(listLessons, dayOfWeek);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                if(response.getError() == null){
                    onDayLessonsAddedLiveData.postValue(response.getData());
                }else{
                    onErrorLiveData.postValue(response.getError());
                }
                response.deleteObservers();
            }
        });
    }
}
