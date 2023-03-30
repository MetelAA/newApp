package com.example.newapp.ui.chat;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.newapp.data.repository.chat.getPossibleNewChatsRepositoryImpl;
import com.example.newapp.domain.models.chatModels.chatInfoForPossibleNewChat;
import com.example.newapp.domain.useCases.chatUseCase.getPossibleNewChatsUseCase;
import com.example.newapp.global.Response;
import com.example.newapp.interfaces.createNewChatViewHolder;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class createNewChatViewModelImpl extends ViewModel implements createNewChatViewHolder {
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private getPossibleNewChatsRepositoryImpl getPossibleNewChatsRepository = new getPossibleNewChatsRepositoryImpl(fStore);
    private getPossibleNewChatsUseCase getPossibleNewChatsUseCase = new getPossibleNewChatsUseCase(getPossibleNewChatsRepository);


    MutableLiveData<String> onErrorLiveData = new MutableLiveData<>();
    MutableLiveData<ArrayList<chatInfoForPossibleNewChat>> onGotPossibleChats = new MutableLiveData<>();
    @Override
    public void getPossibleNewChats(ArrayList<String> existingChatsMemberUID) {
        Response<ArrayList<chatInfoForPossibleNewChat>, String> response = getPossibleNewChatsUseCase.getPossibleNewChats(existingChatsMemberUID);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                if(response.getError() != null){
                    onErrorLiveData.postValue(response.getError());
                    return;
                }
                //Log.d("Aboba", "response data - " + response.getData());
                onGotPossibleChats.setValue(response.getData());
            }
        });
    }
}
