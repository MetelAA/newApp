package com.example.newapp.ui.chat;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.newapp.data.repository.chat.createNewChatRepositoryImpl;
import com.example.newapp.data.repository.chat.getAndObserveMessagesRepositoryImpl;
import com.example.newapp.data.repository.chat.getAndObserveUserStatusRepositoryImpl;
import com.example.newapp.data.repository.chat.sendMessageRepositoryImpl;
import com.example.newapp.domain.models.chatModels.chatMessage;
import com.example.newapp.domain.models.chatModels.createNewChatData;
import com.example.newapp.domain.models.chatModels.message;
import com.example.newapp.domain.models.chatModels.userStatus;
import com.example.newapp.domain.models.repository.chatRepository.createNewChatRepository;
import com.example.newapp.domain.useCases.chatUseCase.createNewChatUseCase;
import com.example.newapp.domain.useCases.chatUseCase.getAndObserveMessagesUseCase;
import com.example.newapp.domain.useCases.chatUseCase.getAndObserveUserStatusUseCase;
import com.example.newapp.domain.useCases.chatUseCase.sendMessageUseCase;
import com.example.newapp.global.Response;
import com.example.newapp.interfaces.chatViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

public class chatViewModelImpl extends ViewModel implements chatViewModel {

    private final FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private final FirebaseStorage fStorage = FirebaseStorage.getInstance();

    private final getAndObserveUserStatusRepositoryImpl getAndObserveUsersStatusEsRepository = new getAndObserveUserStatusRepositoryImpl(fStore);
    private final getAndObserveUserStatusUseCase getAndObserveUserStatusUseCase = new getAndObserveUserStatusUseCase(getAndObserveUsersStatusEsRepository);
    private final getAndObserveMessagesRepositoryImpl getAndObserveMessageRepository = new getAndObserveMessagesRepositoryImpl(fStore);
    private final getAndObserveMessagesUseCase getAndObserveMessagesUseCase = new getAndObserveMessagesUseCase(getAndObserveMessageRepository);
    private final sendMessageRepositoryImpl sendMessageRepository = new sendMessageRepositoryImpl(fStore, fStorage.getReference());
    private final sendMessageUseCase sendMessageUseCase = new sendMessageUseCase(sendMessageRepository);
    private final createNewChatRepositoryImpl createNewChatRepository = new createNewChatRepositoryImpl(fStore);
    private final createNewChatUseCase createNewChatUseCase = new createNewChatUseCase(createNewChatRepository);


    public MutableLiveData<String> onErrorLiveData = new MutableLiveData<>();
    public MutableLiveData<userStatus> comradStatus = new MutableLiveData<>();
    public MutableLiveData<message> newMessages = new MutableLiveData<>();
    public MutableLiveData<ArrayList<message>> previousMessages = new MutableLiveData<>();
    public MutableLiveData<String> onChatCreated = new MutableLiveData<>();


    @Override
    public void getAndObserveComradStatus(String comradUID) { //наблюдение за статусами пользователей в грппах не будет
        Response<userStatus, String> response = getAndObserveUserStatusUseCase.getComradStatus(comradUID);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if (response.getError() == null) {
                    comradStatus.postValue(response.getData());
                } else {
                    onErrorLiveData.postValue(response.getError());
                }
            }
        });

    }

    @Override
    public void getNewMessages(Date startSearchDate) {
        Response<message, String> response = getAndObserveMessagesUseCase.getNewMessages(startSearchDate);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if (response.getError() == null) {
                    newMessages.setValue(response.getData());
                } else {
                    onErrorLiveData.postValue(response.getError());
                }
            }
        });
    }

    @Override
    public void getPreviousMessages(Date startSearchDate) {
        Response<ArrayList<message>, String> response = getAndObserveMessagesUseCase.getPreviousMessages(startSearchDate);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if (response.getError() == null) {
                    previousMessages.setValue(response.getData());
                } else {
                    onErrorLiveData.postValue(response.getError());
                }
                response.deleteObservers();
            }
        });
    }

    @Override
    public void sendMessage(message message) {
        Response<String, String> response = sendMessageUseCase.sendMessage(message);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if(response.getError() != null){
                    onErrorLiveData.postValue(response.getError());
                }
                response.deleteObservers();
            }
        });

    }

    @Override
    public void setChatID(String chatID) {
        sendMessageUseCase.setChatID(chatID);
        getAndObserveMessagesUseCase.setChatID(chatID);
    }


    @Override
    public void createNewChat(createNewChatData chatData) {
        Response<String, String> response = createNewChatUseCase.createNewChat(chatData);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if(response.getError() != null){
                    onErrorLiveData.postValue(response.getError());
                }else{
                    onChatCreated.postValue(response.getData());
                }
                response.deleteObservers();
            }
        });
    }

    @Override
    public void removeListeners() {
        getAndObserveMessagesUseCase.removeListeners();
        getAndObserveUserStatusUseCase.removeListeners();
    }
}
