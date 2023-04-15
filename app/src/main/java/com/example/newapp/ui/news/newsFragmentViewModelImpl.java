package com.example.newapp.ui.news;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.newapp.data.repository.getMonthEventsRepositoryImpl;
import com.example.newapp.domain.models.dayEventsData;
import com.example.newapp.domain.useCases.getMonthEventsUseCase;
import com.example.newapp.global.Response;
import com.example.newapp.interfaces.newsFragmentViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

public class newsFragmentViewModelImpl extends ViewModel implements newsFragmentViewModel {

    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private getMonthEventsRepositoryImpl getMonthEventsRepository = new getMonthEventsRepositoryImpl(fStore);
    private getMonthEventsUseCase getMonthEventsUseCase = new getMonthEventsUseCase(getMonthEventsRepository);

    MutableLiveData<String> onErrorLiveData = new MutableLiveData<>();
    MutableLiveData<Map<String, dayEventsData>> gotMonthEvents = new MutableLiveData<>();

    @Override
    public void getMonthEvents(LocalDate date) {
        Response<Map<String, dayEventsData>, String> response = getMonthEventsUseCase.getMonthEvents(date);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if(response.getError() != null){
                    onErrorLiveData.postValue(response.getError());
                }else{
                    gotMonthEvents.setValue(response.getData());
                }
                response.deleteObservers();
            }
        });
    }
}
