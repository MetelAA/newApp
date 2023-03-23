package com.example.newapp.data.repository.chat;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.example.newapp.domain.models.repository.chatRepository.getAndObserveUsersStatusEsRepository;
import com.example.newapp.domain.models.chatModels.userStatus;
import com.example.newapp.domain.models.chatModels.userStatusWithChangeType;
import com.example.newapp.global.Response;
import com.example.newapp.global.constants;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;


public class getAndObserveUsersStatusEsRepositoryImpl implements getAndObserveUsersStatusEsRepository {
    private FirebaseFirestore fStore;

    public getAndObserveUsersStatusEsRepositoryImpl(FirebaseFirestore fStore) {
        this.fStore = fStore;
    }

    @Override
    public Response<ArrayList<userStatusWithChangeType>, String> getUsersStatusEs(ArrayList<String> userUIDs) {
        Response<ArrayList<userStatusWithChangeType>, String> response = new Response<>();
        ListenerRegistration listenerRegistration = fStore
                .collection(constants.KEY_USER_COLLECTION)
                .whereIn(FieldPath.documentId(), userUIDs)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            response.setError(e.getMessage());
                            return;
                        }
                        if (snapshots == null) {
                            return;
                        }
                        ArrayList<userStatusWithChangeType> userStatuses = new ArrayList<>();
                        int i = 0;
                        for (DocumentChange change : snapshots.getDocumentChanges()) {
                            DocumentSnapshot document = change.getDocument();
                            String userStatus = document.get(constants.KEY_USER_STATUS).toString();
                            userStatuses.add(new userStatusWithChangeType(
                                            new userStatus(
                                                    document.getId(),
                                                    userStatus
                                            ),
                                            change.getType()
                                        )
                                    );
                            if(TextUtils.equals(userStatus, constants.KEY_USER_STATUS_EQUALS_OFFLINE)){
                                userStatuses.get(i).userStatus.setLastTimeSeen(document.getDate(constants.KEY_USER_LAST_TIME_SEEN));
                            }
                            i++;
                        }
                        response.setData(userStatuses);
                    }
                });
        return response;
    }
}


