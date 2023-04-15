package com.example.newapp.data.repository.chat;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.newapp.domain.models.chatModels.chatInfo;
import com.example.newapp.domain.models.chatModels.chatInfoWithSnapshotStatus;
import com.example.newapp.domain.models.chatModels.groupChatInfo;
import com.example.newapp.domain.models.chatModels.messageWithText;
import com.example.newapp.domain.models.chatModels.personChatInfo;
import com.example.newapp.domain.models.repository.chatRepository.getExistingChatsRepository;
import com.example.newapp.global.Response;
import com.example.newapp.global.User;
import com.example.newapp.global.constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class getExistingChatsRepositoryImpl implements getExistingChatsRepository {
    FirebaseFirestore fStore;
    ListenerRegistration listener;

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
                                getLastMessage(chatDocumentReferense, chatInfoWithStat);
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
                                                getLastMessage(chatDocumentReferense, chatInfoWithStat);
                                            }
                                        });
                            }
                            i++;
                        }
            }
        });
        return response;
    }


    private void getLastMessage(DocumentSnapshot chatDocumentRef, chatInfoWithSnapshotStatus chatInfoWithStat){
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
                        //Log.d("Aboba", "/////////////// " + documentSnapshot.getData());
                        if(TextUtils.equals(documentSnapshot.get(constants.KEY_CHAT_MSG_TYPE).toString(), constants.KEY_CHAT_MSG_TYPE_EQUALS_TEXT)){
                            chatInfoWithStat.chatInfo.setMessage(new messageWithText(
                                            documentSnapshot.get(constants.KEY_CHAT_MESSAGE_SENDER_NAME).toString(),
                                            documentSnapshot.get(constants.KEY_CHAT_MESSAGE_SENDER_UID).toString(),
                                            documentSnapshot.get(constants.KEY_CHAT_MESSAGE_TEXT).toString(),
                                            documentSnapshot.getDate(constants.KEY_CHAT_MSG_SENT_TIME)
                                    )
                            );
                        }else{
                            chatInfoWithStat.chatInfo.setMessage(new messageWithText(
                                            documentSnapshot.get(constants.KEY_CHAT_MESSAGE_SENDER_NAME).toString(),
                                            documentSnapshot.get(constants.KEY_CHAT_MESSAGE_SENDER_UID).toString(),
                                            "Photo",
                                            documentSnapshot.getDate(constants.KEY_CHAT_MSG_SENT_TIME)
                                    )
                            );
                        }
                        // Log.d("Aboba",  "onCompleteCounter - " + onCompleteCounter[0] + "     setDataResponseCounter - " + setDataResponseCounter + "         finalI - " + finalI);

                        // Log.d("Aboba", "setResponseValue");
                        response.setData(chatInfoWithStat);
                    }
                });
    }
}