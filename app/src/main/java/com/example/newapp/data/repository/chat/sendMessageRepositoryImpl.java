package com.example.newapp.data.repository.chat;

import androidx.annotation.NonNull;

import com.example.newapp.domain.models.chatModels.message;
import com.example.newapp.domain.models.repository.chatRepository.sendMessageRepository;
import com.example.newapp.global.Response;
import com.example.newapp.global.User;
import com.example.newapp.global.constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class sendMessageRepositoryImpl implements sendMessageRepository {
    private FirebaseFirestore fStore;
    private String chatID;

    public sendMessageRepositoryImpl(FirebaseFirestore fStore) {
        this.fStore = fStore;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    @Override
    public Response<String, String> sendMessage(message message) {
        Response<String, String> response = new Response<>();
        Map<String, Object> messageData = new HashMap<>();
        String messageID = UUID.randomUUID().toString().substring(0, 12);
        messageData.put(constants.KEY_CHAT_MESSAGE, message.messageText);
        messageData.put(constants.KEY_CHAT_MESSAGE_READ_USERS_UIDs, Arrays.asList(User.getUID()));
        messageData.put(constants.KEY_CHAT_MESSAGE_SENDER_NAME, message.senderName);
        messageData.put(constants.KEY_CHAT_MESSAGE_SENDER_UID, message.senderUID);
        messageData.put(constants.KEY_CHAT_MSG_SENT_TIME, message.messageSentTime);
        fStore.collection(constants.KEY_CHAT_COLLECTION).document(chatID).collection(constants.KEY_CHAT_CHAT_MESSAGES_COLLECTION).document(messageID).set(messageData)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        response.setError(e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        response.setData("ok");
                    }
                });
        return response;
    }
}
