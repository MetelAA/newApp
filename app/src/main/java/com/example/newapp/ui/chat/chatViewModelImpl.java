package com.example.newapp.ui.chat;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.newapp.data.repository.chat.getAndObserveMessagesRepositoryImpl;
import com.example.newapp.data.repository.chat.getAndObserveUserStatusRepositoryImpl;
import com.example.newapp.data.repository.chat.sendMessageRepositoryImpl;
import com.example.newapp.domain.models.chatModels.chatMessage;
import com.example.newapp.domain.models.chatModels.userStatus;
import com.example.newapp.domain.useCases.chatUseCase.getAndObserveMessagesUseCase;
import com.example.newapp.domain.useCases.chatUseCase.getAndObserveUserStatusUseCase;
import com.example.newapp.domain.useCases.chatUseCase.sendMessageUseCase;
import com.example.newapp.global.Response;
import com.example.newapp.interfaces.chatViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

public class chatViewModelImpl extends ViewModel implements chatViewModel {

    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    private getAndObserveUserStatusRepositoryImpl getAndObserveUsersStatusEsRepository  = new getAndObserveUserStatusRepositoryImpl(fStore);
    private getAndObserveUserStatusUseCase getAndObserveUserStatusUseCase = new getAndObserveUserStatusUseCase(getAndObserveUsersStatusEsRepository);
    private getAndObserveMessagesRepositoryImpl getAndObserveMessageRepository = new getAndObserveMessagesRepositoryImpl(fStore);
    private getAndObserveMessagesUseCase getAndObserveMessagesUseCase = new getAndObserveMessagesUseCase(getAndObserveMessageRepository);
    private sendMessageRepositoryImpl sendMessageRepository = new sendMessageRepositoryImpl(fStore);
    private sendMessageUseCase sendMessageUseCase = new sendMessageUseCase(sendMessageRepository);


    public MutableLiveData<String> onErrorLiveData = new MutableLiveData<>();
    public MutableLiveData<userStatus> comradStatus = new MutableLiveData<>();


    @Override
    public void getComradStatus(String comradUID) { //наблюдение за статусами пользователей в грппах не будет
        Response<userStatus, String> response = getAndObserveUserStatusUseCase.getComradStatus(comradUID);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if(response.getError() == null){
                    comradStatus.postValue(response.getData());
                }else{
                    onErrorLiveData.postValue(response.getError());
                }
            }
        });

    }

    @Override
    public void getNewMessages(String chatID, Date startSearchDate) {
        Response<ArrayList<chatMessage>, String> response = getAndObserveMessagesUseCase.getNewMessages(startSearchDate, chatID);
        if(response.getError() == null){

        }else{
            onErrorLiveData.postValue(response.getError());
        }
    }

    @Override
    public void getPreviousMessages(String chatID, Date startSearchDate) {
        Response<ArrayList<chatMessage>, String> response = getAndObserveMessagesUseCase.getPreviousMessages(startSearchDate, chatID);
        if(response.getError() == null){

        }else{
            onErrorLiveData.postValue(response.getError());
        }
    }

    @Override
    public void sendMessage() {

    }

    @Override
    public void createNewChat() {

    }

    @Override
    public void removeListeners() {

    }
}
