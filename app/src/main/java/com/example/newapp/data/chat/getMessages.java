package com.example.newapp.data.chat;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.newapp.global.constants;
import com.example.newapp.domain.models.oldModels.textMessagePersonChat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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

import java.util.concurrent.atomic.AtomicBoolean;

public class getMessages {
//
//    CallbackInterfaceWithList callbackGetMessageList;
//    CallbackInterfaceWithList callbackForNewMessages;
//    FirebaseFirestore fStore;
//    String chatId;
//
//    public getMessages(FirebaseFirestore fStore, String chatId, CallbackInterfaceWithList callbackGetMessageList, CallbackInterfaceWithList callbackForNewMessages) {
//        this.callbackGetMessageList = callbackGetMessageList;
//        this.callbackForNewMessages = callbackForNewMessages;
//        this.fStore = fStore;
//        this.chatId = chatId;
//    }
//
//    public void getListMessages(Date startSearchDate, int elementsLimit, String chatDocument) {
//        ArrayList<textMessagePersonChat> messagesList = new ArrayList<>();
//        fStore.collection(constants.KEY_CHAT_COLLECTION)
//                .document(chatDocument)
//                .collection(constants.KEY_CHAT_CHAT_MESSAGES_COLLECTION)
//                .orderBy(constants.KEY_CHAT_MSG_SENT_TIME, Query.Direction.DESCENDING)
//                .whereLessThan(constants.KEY_CHAT_MSG_SENT_TIME, new Timestamp(startSearchDate))
//                .limit(elementsLimit)
//                .get()
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        callbackGetMessageList.throwError("Ошибка получения сообщений");
//                    }
//                })
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
//                                Timestamp timestamp = (Timestamp) document.get(constants.KEY_CHAT_MSG_SENT_TIME);
//                                if (!TextUtils.equals(constants.KEY_CHAT_MSG_TYPE, constants.KEY_CHAT_MSG_TYPE_EQUALS_FILE)) {
//                                    messagesList.add(new textMessagePersonChat(
//                                        document.get(constants.KEY_CHAT_MESSAGE).toString(),
//                                        document.get(constants.KEY_CHAT_MESSAGE_SENDER_UID).toString(),
//                                        (Date) document.get(constants.KEY_CHAT_MSG_SENT_TIME)
//                                    ));
//                                    callbackGetMessageList.requestResult(messagesList);
//                                } else {
//                                    callbackGetMessageList.throwError("Ошибка получения сообщений");
//                                }
//
//                            }
//                        } else {
//                            callbackGetMessageList.throwError("Ошибка получения сообщений");
//                        }
//                    }
//                });
//    }
//
//    ListenerRegistration listener;
//
//    private AtomicBoolean isFirstSnapshot = new AtomicBoolean(true);
//
//    public void setRealTimesUpdates() {
//        listener = fStore.collection(constants.KEY_CHAT_COLLECTION)
//                .document()
//                .collection(constants.KEY_CHAT_CHAT_MESSAGES_COLLECTION)
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                if (error != null) {
//                    callbackForNewMessages.throwError(error.getMessage());
//                }
//                if (isFirstSnapshot.get()) {
//                    isFirstSnapshot.set(false);
//                    return;
//                }
//                ArrayList<textMessagePersonChat> newMessages = new ArrayList<>();
//                if (value != null) {
//                    for (DocumentChange document : value.getDocumentChanges()) {
//                        if (document.getType() == DocumentChange.Type.ADDED) {
//                            newMessages.add(new textMessagePersonChat(
//                                    document.getDocument().get(constants.KEY_CHAT_MESSAGE).toString(),
//                                    document.getDocument().get(constants.KEY_CHAT_MESSAGE_SENDER_UID).toString(),
//                                    (Date) document.getDocument().get(constants.KEY_CHAT_MSG_SENT_TIME)
//                            ));
//                        }
//                    }
//                    callbackForNewMessages.requestResult(newMessages);
//                }
//            }
//        });
//    }
//
//    public void stopListening(){
//        listener.remove();
//    }
}
