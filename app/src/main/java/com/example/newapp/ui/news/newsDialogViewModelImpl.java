package com.example.newapp.ui.news;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.newapp.data.repository.addNewDayEventRepositoryImpl;
import com.example.newapp.domain.models.eventData;
import com.example.newapp.domain.useCases.addNewDayEventUseCase;
import com.example.newapp.global.Response;
import com.example.newapp.interfaces.newsDialogViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.Observable;
import java.util.Observer;

public class newsDialogViewModelImpl extends ViewModel implements newsDialogViewModel {
    private final FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    private addNewDayEventRepositoryImpl addNewDayEventRepository = new addNewDayEventRepositoryImpl(fStore);
    private addNewDayEventUseCase addNewDayEventUseCase = new addNewDayEventUseCase(addNewDayEventRepository);

    public MutableLiveData<String> onError = new MutableLiveData<>();
    public MutableLiveData<eventData> onAdded = new MutableLiveData<>();
    @Override
    public void setNewDayEvent(eventData event, LocalDate date) {
        Response<eventData, String> response = addNewDayEventUseCase.addNewEvent(event, date);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if(response.getError() != null){
                    onError.setValue(response.getError());
                }else{
                    onAdded.setValue(response.getData());
                }
                response.deleteObservers();
            }
        });
    }
}
