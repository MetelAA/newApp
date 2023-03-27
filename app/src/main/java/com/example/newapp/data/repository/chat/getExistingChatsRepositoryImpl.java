package com.example.newapp.data.repository.chat;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.newapp.domain.models.chatModels.chatInfo;
import com.example.newapp.domain.models.chatModels.chatInfoWithSnapshotStatus;
import com.example.newapp.domain.models.chatModels.groupChatInfo;
import com.example.newapp.domain.models.chatModels.message;
import com.example.newapp.domain.models.chatModels.personChatInfo;
import com.example.newapp.domain.models.repository.chatRepository.getExistingChatsRepository;
import com.example.newapp.global.Response;
import com.example.newapp.global.User;
import com.example.newapp.global.constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.List;

public class getExistingChatsRepositoryImpl implements getExistingChatsRepository {
    FirebaseFirestore fStore;
    ListenerRegistration listener;

   private int  setDataResponseCounter = 2;  //то число которого надо достичь чтобы сделать setData
    int i = 0;
    chatInfoWithSnapshotStatus chatInfoWithStat;
    Response<chatInfoWithSnapshotStatus, String> response = new Response<>();
    public getExistingChatsRepositoryImpl(FirebaseFirestore fStore) {
        this.fStore = fStore;
    }
    @Override
    public Response<chatInfoWithSnapshotStatus, String> getExistingChats() {

        listener = fStore.collection(constants.KEY_CHAT_COLLECTION)
                .whereArrayContains(constants.KEY_CHAT_MEMBERS_UIDs, User.getUID())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {

                        if(e != null){
                            response.setError(e.getMessage());
                            return;
                        }
                        if(snapshots == null){
                            return;
                        }

                        for (DocumentChange change : snapshots.getDocumentChanges()) {
                            DocumentSnapshot chatDocumentReferense = change.getDocument();
                            //Log.d("Aboba", chatDocumentReferense.getData().toString());
                            if(change.getType() == DocumentChange.Type.REMOVED){
                                response.setData(new chatInfoWithSnapshotStatus(
                                        new chatInfo(
                                            chatDocumentReferense.get(constants.KEY_CHAT_CHAT_ID).toString()
                                         ),
                                        change.getType()
                                    )
                                );
                                return;
                            }

                            if(chatDocumentReferense.get(constants.KEY_CHAT_LAST_MESSAGE_REFERENCE) == null){
                                return;
                            }

                            if(TextUtils.equals(chatDocumentReferense.get(constants.KEY_CHAT_TYPE).toString(), constants.KEY_CHAT_TYPE_EQUALS_GROUP_CHAT)){
                                chatInfoWithStat = new chatInfoWithSnapshotStatus(
                                        new groupChatInfo(
                                                constants.KEY_CHAT_TYPE_EQUALS_GROUP_CHAT,
                                                chatDocumentReferense.get(constants.KEY_CHAT_CHAT_ID).toString(),
                                                chatDocumentReferense.get(constants.KEY_CHAT_GROUP_CHAT_TITLE).toString()
                                        ),
                                        change.getType()
                                );
                                getLastMessageAndMessagesCount(chatDocumentReferense, chatInfoWithStat);
                            }else{
                                List<String> chatMembersUIDs = (List<String>) chatDocumentReferense.get(constants.KEY_CHAT_MEMBERS_UIDs);
                                String comradUID = "";
                                if(TextUtils.equals(User.getUID(), chatMembersUIDs.get(0))) comradUID = chatMembersUIDs.get(1); else comradUID = chatMembersUIDs.get(0);

                                String finalComradUID = comradUID;
                                fStore.collection(constants.KEY_USER_COLLECTION).document(comradUID).get()
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                response.setError(e.getMessage());
                                            }
                                        })
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot comradDocumentRef) {
                                                String comradProfileImage = null;
                                                if(comradDocumentRef.get(constants.KEY_PROFILE_IMAGE) != null){
                                                    comradProfileImage = comradDocumentRef.get(constants.KEY_PROFILE_IMAGE).toString();
                                                }
                                                //Log.d("Aboba", chatDocumentReferense.get(constants.KEY_CHAT_CHAT_ID).toString() + "        " + finalComradUID + "             " + comradDocumentRef.get(constants.KEY_USER_NAME).toString());
                                                chatInfoWithStat = new chatInfoWithSnapshotStatus(
                                                        new personChatInfo(
                                                                constants.KEY_CHAT_TYPE_EQUALS_PERSON_CHAT,
                                                                chatDocumentReferense.get(constants.KEY_CHAT_CHAT_ID).toString(),
                                                                finalComradUID,
                                                                comradDocumentRef.get(constants.KEY_USER_NAME).toString(),
                                                                comradProfileImage
                                                        ),
                                                        change.getType()
                                                );
                                                getLastMessageAndMessagesCount(chatDocumentReferense, chatInfoWithStat);
                                            }
                                        });
                            }
                            i++;
                        }
            }
        });
        return response;
    }


    private void getLastMessageAndMessagesCount(DocumentSnapshot chatDocumentRef, chatInfoWithSnapshotStatus chatInfoWithStat){
        int finalI = i;
        final int[] onCompleteCounter = {0};
       // Log.d("Aboba", "getLastMessageAndMessagesCount " +  "onCompleteCounter - " + onCompleteCounter[0] + "     setDataResponseCounter - " + setDataResponseCounter);
        //Log.d("Aboba", "onCompCounter = 0   " + onCompleteCounter[0]);
        chatDocumentRef.getDocumentReference(constants.KEY_CHAT_LAST_MESSAGE_REFERENCE).get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        response.setError(e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        chatInfoWithStat.chatInfo.setMessage(new message(
                                        documentSnapshot.get(constants.KEY_CHAT_MESSAGE_SENDER_NAME).toString(),
                                        documentSnapshot.get(constants.KEY_CHAT_MESSAGE_SENDER_UID).toString(),
                                        documentSnapshot.get(constants.KEY_CHAT_MESSAGE).toString(),
                                        documentSnapshot.getDate(constants.KEY_CHAT_MSG_SENT_TIME)
                                )
                        );
                        onCompleteCounter[0]++;
                       // Log.d("Aboba",  "onCompleteCounter - " + onCompleteCounter[0] + "     setDataResponseCounter - " + setDataResponseCounter + "         finalI - " + finalI);
                        if(setDataResponseCounter == onCompleteCounter[0]){
                           // Log.d("Aboba", "setResponseValue");
                            response.setData(chatInfoWithStat);
                        }
                    }
                });
        fStore.collection(constants.KEY_CHAT_COLLECTION)
                .document(chatInfoWithStat.chatInfo.chatID)
                .collection(constants.KEY_CHAT_CHAT_MESSAGES_COLLECTION)
                .orderBy(constants.KEY_CHAT_MSG_SENT_TIME, Query.Direction.DESCENDING)
                .limit(999)
                .count()
                .get(AggregateSource.SERVER)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        response.setError(e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<AggregateQuerySnapshot>() {
                    @Override
                    public void onSuccess(AggregateQuerySnapshot aggregateQuerySnapshot) {
                        long msgCount = aggregateQuerySnapshot.getCount();
                        fStore.collection(constants.KEY_CHAT_COLLECTION)
                                .document(chatInfoWithStat.chatInfo.chatID)
                                .collection(constants.KEY_CHAT_CHAT_MESSAGES_COLLECTION)
                                .orderBy(constants.KEY_CHAT_MSG_SENT_TIME, Query.Direction.DESCENDING)
                                .limit(999)
                                .whereArrayContainsAny(constants.KEY_CHAT_MESSAGE_READ_USERS_UIDs, Arrays.asList(User.getUID()))
                                .count()
                                .get(AggregateSource.SERVER)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        response.setError(e.getMessage());
                                    }
                                })
                                .addOnSuccessListener(new OnSuccessListener<AggregateQuerySnapshot>() {
                                    @Override
                                    public void onSuccess(AggregateQuerySnapshot aggregateQuerySnapshot) {
                                        chatInfoWithStat.chatInfo.setUnreadMessageCount(msgCount - aggregateQuerySnapshot.getCount());
                                        onCompleteCounter[0]++;
                                        //Log.d("Aboba",  "onCompleteCounter - " + onCompleteCounter[0] + "     setDataResponseCounter - " + setDataResponseCounter + "         finalI - " + finalI);
                                        if(setDataResponseCounter == onCompleteCounter[0]){
                                           //Log.d("Aboba", "setResponseValue");
                                            response.setData(chatInfoWithStat);
                                        }
                                    }
                                });
                    }
                });
    }
}