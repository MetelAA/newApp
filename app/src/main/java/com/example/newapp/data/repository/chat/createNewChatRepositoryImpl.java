package com.example.newapp.data.repository.chat;

import androidx.annotation.NonNull;

import com.example.newapp.domain.models.chatModels.createNewChatData;
import com.example.newapp.domain.models.repository.chatRepository.createNewChatRepository;
import com.example.newapp.global.Response;
import com.example.newapp.global.User;
import com.example.newapp.global.constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
        String chatID = UUID.randomUUID().toString().substring(0, 20);
        Map<String, Object> chatData = new HashMap<>();
        chatData.put(constants.KEY_CHAT_CHAT_ID, chatID);
        chatData.put(constants.KEY_CHAT_MEMBERS_UIDs, newChatData.chatMembersUID);
        chatData.put(constants.KEY_CHAT_TYPE, newChatData.chatType);
        fStore.collection(constants.KEY_CHAT_COLLECTION).document(chatID).set(chatData)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        response.setError(e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        response.setData(chatID);
                    }
                });

        return response;
    }
}
