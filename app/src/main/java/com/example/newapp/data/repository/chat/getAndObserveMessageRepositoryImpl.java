package com.example.newapp.data.repository.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.newapp.domain.models.chatModels.chatMessage;

import com.example.newapp.domain.models.repository.chatRepository.getAndObserveMessagesRepository;
import com.example.newapp.global.Response;
import com.example.newapp.global.constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class getAndObserveMessageRepositoryImpl implements getAndObserveMessagesRepository {
    private FirebaseFirestore fStore;
    private String chatUID;

    public getAndObserveMessageRepositoryImpl(FirebaseFirestore fStore, String chatUID) {
        this.fStore = fStore;
        this.chatUID = chatUID;
    }

    @Override
    public Response<ArrayList<chatMessage>, String> getPreviousMessages(Date startSearchDate) {
        Response<ArrayList<chatMessage>, String> response = new Response<>();

        fStore.collection(constants.KEY_CHAT_COLLECTION)
                .document(chatUID)
                .collection(constants.KEY_CHAT_CHAT_MESSAGES_COLLECTION)
                .orderBy(constants.KEY_CHAT_MSG_SENT_TIME, Query.Direction.DESCENDING)
                .whereLessThan(constants.KEY_CHAT_MSG_SENT_TIME, new Timestamp(startSearchDate))
                .limit(99)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        response.setError(e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<chatMessage> chatMessagesListForShowing = new ArrayList<>();
                        int i = 0;
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            chatMessagesListForShowing.add(new chatMessage(
                                    document.getId(),
                                    document.get(constants.KEY_CHAT_MESSAGE_SENDER_NAME).toString(),
                                    document.get(constants.KEY_CHAT_MESSAGE_SENDER_UID).toString(),
                                    document.get(constants.KEY_CHAT_MESSAGE).toString(),
                                    document.getDate(constants.KEY_CHAT_MSG_SENT_TIME)
                            ));
                            if(((ArrayList<String>) document.get(constants.KEY_CHAT_MESSAGE_READ_USERS_UIDs)).size() > 1){
                                chatMessagesListForShowing.get(i).setMessageStatus(constants.KEY_CHAT_MESSAGE_STATUS_READ);
                            }else{
                                chatMessagesListForShowing.get(i).setMessageStatus(constants.KEY_CHAT_MESSAGE_STATUS_UNREAD);
                            }
                            i++;
                        }
                        response.setData(chatMessagesListForShowing);
                    }
                });
        return response;
    }
    ListenerRegistration listener;
    @Override
    public Response<ArrayList<chatMessage>, String> getNewMessages(Date startObserveDate) { //пока у нас можно только добавлять сообещения
        Response<ArrayList<chatMessage>, String> response = new Response<>();

         listener = fStore.collection(constants.KEY_CHAT_COLLECTION)
                .document(chatUID)
                .collection(constants.KEY_CHAT_CHAT_MESSAGES_COLLECTION)
                .orderBy(constants.KEY_CHAT_MSG_SENT_TIME, Query.Direction.DESCENDING)
                .whereGreaterThan(constants.KEY_CHAT_MSG_SENT_TIME, new Timestamp(startObserveDate))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            response.setError(error.getMessage());
                        }
                        if(snapshots == null){
                            return;
                        }
                        ArrayList<chatMessage> chatMessagesListForShowing = new ArrayList<>();
                        int i = 0;
                        for (DocumentSnapshot document : snapshots.getDocuments()){
                            chatMessagesListForShowing.add(new chatMessage(
                                    document.getId(),
                                    document.get(constants.KEY_CHAT_MESSAGE_SENDER_NAME).toString(),
                                    document.get(constants.KEY_CHAT_MESSAGE_SENDER_UID).toString(),
                                    document.get(constants.KEY_CHAT_MESSAGE).toString(),
                                    document.getDate(constants.KEY_CHAT_MSG_SENT_TIME)
                            ));
                            if(((ArrayList<String>) document.get(constants.KEY_CHAT_MESSAGE_READ_USERS_UIDs)).size() > 1){
                                chatMessagesListForShowing.get(i).setMessageStatus(constants.KEY_CHAT_MESSAGE_STATUS_READ);
                            }else{
                                chatMessagesListForShowing.get(i).setMessageStatus(constants.KEY_CHAT_MESSAGE_STATUS_UNREAD);
                            }
                            i++;
                        }
                        response.setData(chatMessagesListForShowing);
                    }
                });

        return response;
    }
}
