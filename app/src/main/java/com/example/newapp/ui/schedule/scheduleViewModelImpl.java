package com.example.newapp.ui.schedule;



import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.newapp.data.repository.getDayLessonsRepositoryImpl;
import com.example.newapp.domain.models.arrayListForSchedule;
import com.example.newapp.domain.useCases.getDayLessonsUseCase;
import com.example.newapp.global.Response;
import com.example.newapp.interfaces.scheduleViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Observable;
import java.util.Observer;

public class scheduleViewModelImpl extends ViewModel implements scheduleViewModel {
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    private final getDayLessonsRepositoryImpl getDayLessonsRepository = new getDayLessonsRepositoryImpl(fStore);
    private final getDayLessonsUseCase getDayLessonsUseCase = new getDayLessonsUseCase(getDayLessonsRepository);

    MutableLiveData<arrayListForSchedule> onGotDayLessonsLiveData = new MutableLiveData<>();
    MutableLiveData<String> onErrorLiveData = new MutableLiveData<>();

    @Override
    public void getDayLessons(String dayOfWeek) {
        Response<arrayListForSchedule, String> response = getDayLessonsUseCase.getDayLessons(dayOfWeek);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                if(response.getError() == null){
                    onGotDayLessonsLiveData.setValue(response.getData());
                }else{
                    onErrorLiveData.setValue(response.getError());
                }
                response.deleteObservers();
            }
        });
    }
}
