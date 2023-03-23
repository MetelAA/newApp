package com.example.newapp.ui.chat;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.newapp.data.repository.chat.getExistingChatsRepositoryImpl;
import com.example.newapp.domain.models.chatModels.chatInfoWithSnapshotStatus;
import com.example.newapp.domain.useCases.chatUseCase.getExistingChatsUseCase;
import com.example.newapp.global.Response;
import com.example.newapp.interfaces.showChatsViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class showChatsViewModelImpl extends ViewModel implements showChatsViewModel {

    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private getExistingChatsRepositoryImpl getExistingChatsRepository = new getExistingChatsRepositoryImpl(fStore);
    private getExistingChatsUseCase getExistingChatsUseCase = new getExistingChatsUseCase(getExistingChatsRepository);

    public MutableLiveData<String> onErrorLiveData = new MutableLiveData<>();
    public MutableLiveData<ArrayList<chatInfoWithSnapshotStatus>> gotListExitingChats = new MutableLiveData<>();
    public boolean isListenerActiveFlag = false;



    @Override
    public void getExistingChats() {
        isListenerActiveFlag = true;
        Response<ArrayList<chatInfoWithSnapshotStatus>, String> response = getExistingChatsUseCase.getExistingChat();
        response.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                if(response.getError() != null){
                    onErrorLiveData.postValue(response.getError());
                    return;
                }

                gotListExitingChats.postValue(response.getData());
            }
        });
    }
}