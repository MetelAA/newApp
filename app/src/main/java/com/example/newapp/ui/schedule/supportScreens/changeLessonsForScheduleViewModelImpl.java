package com.example.newapp.ui.schedule.supportScreens;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.newapp.data.repository.addDeleteLessonDescriptionRepositoryImpl;
import com.example.newapp.data.repository.getLessonsDescriptionRepositoryImpl;
import com.example.newapp.domain.models.lessonDescription;
import com.example.newapp.domain.useCases.addDeleteLessonDescriptionUseCase;
import com.example.newapp.domain.useCases.getLessonsDescriptionUseCase;
import com.example.newapp.global.Response;
import com.example.newapp.interfaces.changeLessonsForScheduleViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class changeLessonsForScheduleViewModelImpl extends ViewModel implements changeLessonsForScheduleViewModel {

    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    private final getLessonsDescriptionRepositoryImpl getLessonsDescriptionRepository = new getLessonsDescriptionRepositoryImpl(fStore);
    private final addDeleteLessonDescriptionRepositoryImpl addDeleteLessonDescriptionRepository = new addDeleteLessonDescriptionRepositoryImpl(fStore);
    private final getLessonsDescriptionUseCase getLessonsDescriptionUseCase = new getLessonsDescriptionUseCase(getLessonsDescriptionRepository);
    private final addDeleteLessonDescriptionUseCase addDeleteLessonDescriptionUseCase = new addDeleteLessonDescriptionUseCase(addDeleteLessonDescriptionRepository);

    MutableLiveData<String> onErrorLiveData = new MutableLiveData<>();
    MutableLiveData<ArrayList<lessonDescription>> gotLessonsDescriptionLiveData = new MutableLiveData<>();
    MutableLiveData<lessonDescription> addedLessonDescriptionListLiveData = new MutableLiveData<>();
    MutableLiveData<lessonDescription> deletedLessonDescriptionLiveData = new MutableLiveData<>();



    @Override
    public void getLessonsDescription() {
        Response<ArrayList<lessonDescription>, String> response = getLessonsDescriptionUseCase.getLessonsDescription();
        response.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                if (response.getError() == null) {
                    gotLessonsDescriptionLiveData.postValue(response.getData());
                } else {
                    onErrorLiveData.postValue(response.getError());
                }
                response.deleteObservers();
            }
        });
    }

    @Override
    public void addLessonDescription(lessonDescription lessonDescription) {
        Response<lessonDescription, String> response = addDeleteLessonDescriptionUseCase.addLessonDescription(lessonDescription);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                if (response.getError() == null) {
                    addedLessonDescriptionListLiveData.postValue(response.getData());
                } else {
                    onErrorLiveData.postValue(response.getError());
                }
                response.deleteObservers();
            }
        });

    }

    @Override
    public void deleteLessonDescription(lessonDescription lessonDescription) {
        Response<lessonDescription, String> response = addDeleteLessonDescriptionUseCase.deleteLessonDescription(lessonDescription);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                if (response.getError() == null) {
                    deletedLessonDescriptionLiveData.postValue(response.getData());
                } else {
                    onErrorLiveData.postValue(response.getError());
                }
                response.deleteObservers();
            }
        });

    }
}
