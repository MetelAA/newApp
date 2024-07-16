package com.example.newapp.ui.chat;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.newapp.data.repository.chat.getExistingChatsRepositoryImpl;
import com.example.newapp.domain.models.chatModels.chatInfo;
import com.example.newapp.domain.models.chatModels.chatInfoWithPositionInList;
import com.example.newapp.domain.models.chatModels.chatInfoWithSnapshotStatus;
import com.example.newapp.domain.useCases.chatUseCase.getExistingChatsUseCase;
import com.example.newapp.global.Response;
import com.example.newapp.interfaces.showChatsViewModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class showChatsViewModelImpl extends ViewModel implements showChatsViewModel {

    private final FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private final getExistingChatsRepositoryImpl getExistingChatsRepository = new getExistingChatsRepositoryImpl(fStore);
    private final getExistingChatsUseCase getExistingChatsUseCase = new getExistingChatsUseCase(getExistingChatsRepository);

    public MutableLiveData<String> onErrorLiveData = new MutableLiveData<>();

    public MutableLiveData<chatInfo> onChatAdded = new MutableLiveData<>();
    public MutableLiveData<chatInfoWithPositionInList> onChatModified = new MutableLiveData<>();
    public MutableLiveData<chatInfo> onChatRemoved = new MutableLiveData<>();
    public boolean isListenerActiveFlag = false;

    public ArrayList<chatInfo> chatInfosList = new ArrayList<>();


    @Override
    public void getExistingChats() {
        isListenerActiveFlag = true;
        Response<chatInfoWithSnapshotStatus, String> response = getExistingChatsUseCase.getExistingChat();
        response.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                if(response.getError() != null){
                    onErrorLiveData.postValue(response.getError());
                    return;
                }
                chatInfoWithSnapshotStatus chatInfoWithSnapshotStatus = response.getData();
                switch (chatInfoWithSnapshotStatus.changeType) {
                    case ADDED:
                        //Log.d("Aboba", "ViewModel chat added " + chatInfoWithSnapshotStatus.chatInfo);
                        chatInfosList.add(chatInfoWithSnapshotStatus.chatInfo);
                        onChatAdded.setValue(chatInfoWithSnapshotStatus.chatInfo);
                        break;
                    case MODIFIED:
                        //Log.d("Aboba", "ViewModel chat modified" + chatInfoWithSnapshotStatus.chatInfo);
                        chatInfo changedChat = chatInfosList.stream()
                                .filter(chat -> chatInfoWithSnapshotStatus.chatInfo.chatID.equals(chat.chatID))
                                .findAny()
                                .orElse(null);
                        if(changedChat == null){
                            chatInfosList.add(chatInfoWithSnapshotStatus.chatInfo);
                            onChatAdded.setValue(chatInfoWithSnapshotStatus.chatInfo);
                        }else{
                            int changeChatPosition = chatInfosList.indexOf(changedChat);
                            chatInfosList.set(changeChatPosition, chatInfoWithSnapshotStatus.chatInfo);
                            onChatModified.setValue(new chatInfoWithPositionInList(chatInfoWithSnapshotStatus.chatInfo, changeChatPosition));
                        }
                        break;
                    case REMOVED:
                        //Log.d("Aboba", "ViewModel chat rewmoved" + chatInfoWithSnapshotStatus.chatInfo);
                        chatInfo removingChat = chatInfosList.stream()
                                .filter(chat -> chatInfoWithSnapshotStatus.chatInfo.chatID.equals(chat.chatID))
                                .findAny()
                                .orElse(null);
                        chatInfosList.remove(removingChat);
                        onChatRemoved.setValue(removingChat);
                        break;
                }

            }
        });
    }
}
