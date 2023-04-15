package com.example.newapp.data.repository.chat;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.newapp.domain.models.chatModels.chatMessage;

import com.example.newapp.domain.models.chatModels.chatMessageWithImage;
import com.example.newapp.domain.models.chatModels.chatMessageWithText;
import com.example.newapp.domain.models.chatModels.message;
import com.example.newapp.domain.models.repository.chatRepository.getAndObserveMessagesRepository;
import com.example.newapp.global.Response;
import com.example.newapp.global.User;
import com.example.newapp.global.constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class getAndObserveMessagesRepositoryImpl implements getAndObserveMessagesRepository {
    private FirebaseFirestore fStore;
    private String chatID;


    public getAndObserveMessagesRepositoryImpl(FirebaseFirestore fStore) {
        this.fStore = fStore;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    @Override
    public Response<ArrayList<message>, String> getPreviousMessages(Date startSearchDate) {
        Response<ArrayList<message>, String> response = new Response<>();

        fStore.collection(constants.KEY_CHAT_COLLECTION)
                .document(chatID)
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
                        ArrayList<message> chatMessagesList = new ArrayList<>();
                        List<DocumentSnapshot> documentsList = queryDocumentSnapshots.getDocuments();

                        for (int i = documentsList.size() - 1; i >= 0; i--) {
                            DocumentSnapshot document = documentsList.get(i);
                            if (TextUtils.equals(document.get(constants.KEY_CHAT_MSG_TYPE).toString(), constants.KEY_CHAT_MSG_TYPE_EQUALS_TEXT)) {
                                chatMessageWithText textMessage = new chatMessageWithText(
                                        document.getId(),
                                        document.get(constants.KEY_CHAT_MESSAGE_SENDER_NAME).toString(),
                                        document.get(constants.KEY_CHAT_MESSAGE_SENDER_UID).toString(),
                                        document.get(constants.KEY_CHAT_MESSAGE_TEXT).toString(),
                                        document.getDate(constants.KEY_CHAT_MSG_SENT_TIME)
                                );
                                chatMessagesList.add(textMessage);
                            } else {
                                chatMessageWithImage imageMessage = new chatMessageWithImage(
                                        document.getId(),
                                        document.get(constants.KEY_CHAT_MESSAGE_SENDER_NAME).toString(),
                                        document.get(constants.KEY_CHAT_MESSAGE_SENDER_UID).toString(),
                                        document.get(constants.KEY_CHAT_MESSAGE_IMAGE_URL).toString(),
                                        document.getDate(constants.KEY_CHAT_MSG_SENT_TIME)
                                );
                                chatMessagesList.add(imageMessage);
                            }

                        }
                        response.setData(chatMessagesList);
                    }
                });
        return response;
    }
    ListenerRegistration listener;

    @Override
    public Response<message, String> getNewMessages(Date startObserveDate) { //пока у нас можно только добавлять сообещения
        Response<message, String> response = new Response<>();

        listener = fStore.collection(constants.KEY_CHAT_COLLECTION)
                .document(chatID)
                .collection(constants.KEY_CHAT_CHAT_MESSAGES_COLLECTION)
                .whereGreaterThan(constants.KEY_CHAT_MSG_SENT_TIME, new Timestamp(startObserveDate))
                .orderBy(constants.KEY_CHAT_MSG_SENT_TIME, Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            response.setError(error.getMessage());
                        }
                        if (snapshots == null) {
                            return;
                        }


                        for (DocumentChange documentChange : snapshots.getDocumentChanges()) {
                            DocumentSnapshot document = documentChange.getDocument();
                            if (TextUtils.equals(document.get(constants.KEY_CHAT_MSG_TYPE).toString(), constants.KEY_CHAT_MSG_TYPE_EQUALS_TEXT)) {
                                chatMessageWithText textMessage = new chatMessageWithText(
                                        document.getId(),
                                        document.get(constants.KEY_CHAT_MESSAGE_SENDER_NAME).toString(),
                                        document.get(constants.KEY_CHAT_MESSAGE_SENDER_UID).toString(),
                                        document.get(constants.KEY_CHAT_MESSAGE_TEXT).toString(),
                                        document.getDate(constants.KEY_CHAT_MSG_SENT_TIME)
                                );
                                response.setData(textMessage);
                            } else {
                                chatMessageWithImage imageMessage = new chatMessageWithImage(
                                        document.getId(),
                                        document.get(constants.KEY_CHAT_MESSAGE_SENDER_NAME).toString(),
                                        document.get(constants.KEY_CHAT_MESSAGE_SENDER_UID).toString(),
                                        document.get(constants.KEY_CHAT_MESSAGE_IMAGE_URL).toString(),
                                        document.getDate(constants.KEY_CHAT_MSG_SENT_TIME)
                                );

                                response.setData(imageMessage);
                            }
                        }

                    }
                });
        return response;
    }

    public void removeListener() {
        if(listener != null){
            listener.remove();
        }
    }
}
