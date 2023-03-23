package com.example.newapp.data.repository.chat;

import androidx.annotation.NonNull;

import com.example.newapp.domain.models.chatModels.createNewChatData;
import com.example.newapp.domain.models.repository.chatRepository.createNewChatRepository;
import com.example.newapp.global.Response;
import com.example.newapp.global.User;
import com.example.newapp.global.constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class createNewChatRepositoryImpl implements createNewChatRepository {
    FirebaseFirestore fStore;

    public createNewChatRepositoryImpl(FirebaseFirestore fStore) {
        this.fStore = fStore;
    }

    @Override
    public Response<String, String> createNewChat(createNewChatData newChatData) {
        Response<String, String> response = new Response<>();
        String chatID = UUID.randomUUID().toString().substring(0, 12);
        Map<String, Object> chatData = new HashMap<>();
        chatData.put(constants.KEY_CHAT_CHAT_ID, chatID);
        chatData.put(constants.KEY_CHAT_MEMBERS_NAMEs, newChatData.chatMembersNames);
        chatData.put(constants.KEY_CHAT_MEMBERS_UIDs, newChatData.chatMembersUID);
        chatData.put(constants.KEY_CHAT_TYPE, newChatData.chatType);
        fStore.collection(constants.KEY_CHAT_COLLECTION).document(chatID).set(chatData)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        response.setError(e.getMessage());
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Map<String, Object> messageData = new HashMap<>();
                            messageData.put(constants.KEY_CHAT_MESSAGE, newChatData.message.messageText);
                            messageData.put(constants.KEY_CHAT_MESSAGE_READ_USERS_UIDs, Arrays.asList(User.getUID()));
                            messageData.put(constants.KEY_CHAT_MESSAGE_SENDER_NAME, newChatData.message.senderName);
                            messageData.put(constants.KEY_CHAT_MESSAGE_SENDER_UID, newChatData.message.senderUID);
                            messageData.put(constants.KEY_CHAT_MSG_SENT_TIME, newChatData.message.messageSentTime);
                            fStore.collection(constants.KEY_CHAT_COLLECTION).document(chatID).collection(constants.KEY_CHAT_CHAT_MESSAGES_COLLECTION).add(messageData)
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            response.setError(e.getMessage());
                                        }
                                    })
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            fStore.collection(constants.KEY_CHAT_COLLECTION).document(chatID).update(constants.KEY_CHAT_LAST_MESSAGE_REFERENCE, documentReference)
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            response.setError(e.getMessage());
                                                        }
                                                    })
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                response.setData("ok");
                                                            }else{
                                                                response.setError(task.getException().getMessage());
                                                            }
                                                        }
                                                    });
                                        }
                                    });
                        }else{
                            response.setError(task.getException().getMessage());
                        }
                    }
                });

        return response;
    }
}
