package com.example.newapp.data.repository.chat;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.example.newapp.domain.models.repository.chatRepository.getAndObserveUserStatusRepository;
import com.example.newapp.domain.models.chatModels.userStatus;
import com.example.newapp.global.Response;
import com.example.newapp.global.constants;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

public class getAndObserveUserStatusRepositoryImpl implements getAndObserveUserStatusRepository {
    private FirebaseFirestore fStore;
    private ListenerRegistration listener;

    public getAndObserveUserStatusRepositoryImpl(FirebaseFirestore fStore) {
        this.fStore = fStore;
    }

    @Override
    public Response<userStatus, String> getComradStatus(String comradUID) {
        Response<userStatus, String> response = new Response<>();
        listener = fStore
                .collection(constants.KEY_USER_COLLECTION)
                .document(comradUID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot document, @Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            response.setError(e.getMessage());
                            return;
                        }
                        if (document == null) {
                            return;
                        }
                        String userStatusStr = document.get(constants.KEY_USER_STATUS).toString();
                        userStatus userStatus = new userStatus(
                                document.getId(),
                                userStatusStr
                        );
                        if(TextUtils.equals(userStatusStr, constants.KEY_USER_STATUS_EQUALS_OFFLINE)){
                            userStatus.setLastTimeSeen(document.getDate(constants.KEY_USER_LAST_TIME_SEEN));
                        }
                        response.setData(userStatus);
                    }
                });
        return response;
    }

    public void removeListener(){
        if(listener != null){
            listener.remove();
        }
    }
}


