package com.example.newapp.data.repository.chat;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.newapp.domain.models.chatModels.chatMessageWithText;
import com.example.newapp.domain.models.chatModels.imageMessageForSend;
import com.example.newapp.domain.models.chatModels.message;
import com.example.newapp.domain.models.chatModels.textMessageForSend;
import com.example.newapp.domain.models.repository.chatRepository.sendMessageRepository;
import com.example.newapp.global.Response;
import com.example.newapp.global.User;
import com.example.newapp.global.constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class sendMessageRepositoryImpl implements sendMessageRepository {
    private FirebaseFirestore fStore;
    private StorageReference storageRef;
    private String chatID;

    public sendMessageRepositoryImpl(FirebaseFirestore fStore, StorageReference storageRef) {
        this.fStore = fStore;
        this.storageRef = storageRef;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    @Override
    public Response<String, String> sendTextMessage(textMessageForSend message) {
        Response<String, String> response = new Response<>();
        Map<String, Object> messageData = new HashMap<>();
        String messageID = UUID.randomUUID().toString().substring(0, 25);
        messageData.put(constants.KEY_CHAT_MESSAGE_READ_USERS_UIDs, Arrays.asList(User.getUID()));
        messageData.put(constants.KEY_CHAT_MESSAGE_SENDER_NAME, message.senderName);
        messageData.put(constants.KEY_CHAT_MESSAGE_SENDER_UID, message.senderUID);
        messageData.put(constants.KEY_CHAT_MSG_SENT_TIME, message.messageSentTime);
        messageData.put(constants.KEY_CHAT_MSG_TYPE, constants.KEY_CHAT_MSG_TYPE_EQUALS_TEXT);
        messageData.put(constants.KEY_CHAT_MESSAGE_TEXT, message.messageText);

        DocumentReference docRef =  fStore.collection(constants.KEY_CHAT_COLLECTION).document(chatID).collection(constants.KEY_CHAT_CHAT_MESSAGES_COLLECTION).document(messageID);
        docRef.set(messageData)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        response.setError(e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        fStore.collection(constants.KEY_CHAT_COLLECTION).document(chatID).update(constants.KEY_CHAT_LAST_MESSAGE_REFERENCE, docRef)
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
                    }
                });
        return response;
    }

    @Override
    public Response<String, String> sendImageMessage(imageMessageForSend message) {
        Response<String, String> response = new Response<>();
        String imageName = UUID.randomUUID().toString().substring(0, 20);
        StorageReference path = storageRef.child(constants.KEY_STORAGE_COLLECTION_CHAT_IMAGES).child(chatID).child(imageName);
        String messageID = UUID.randomUUID().toString().substring(0, 25);
        path.putFile(message.imageURL)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        response.setError(e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        path.getDownloadUrl()
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        response.setError(e.getMessage());
                                    }
                                })
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Map<String, Object> messageData = new HashMap<>();
                                        messageData.put(constants.KEY_CHAT_MESSAGE_READ_USERS_UIDs, Arrays.asList(User.getUID()));
                                        messageData.put(constants.KEY_CHAT_MESSAGE_SENDER_NAME, message.senderName);
                                        messageData.put(constants.KEY_CHAT_MESSAGE_SENDER_UID, message.senderUID);
                                        messageData.put(constants.KEY_CHAT_MSG_SENT_TIME, message.messageSentTime);
                                        messageData.put(constants.KEY_CHAT_MSG_TYPE, constants.KEY_CHAT_MSG_TYPE_EQUALS_IMAGE);
                                        messageData.put(constants.KEY_CHAT_MESSAGE_IMAGE_URL, uri.toString());
                                        DocumentReference docRef = fStore.collection(constants.KEY_CHAT_COLLECTION).document(chatID).collection(constants.KEY_CHAT_CHAT_MESSAGES_COLLECTION).document(messageID);
                                        docRef.set(messageData)
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        response.setError(e.getMessage());
                                                    }
                                                })
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        fStore.collection(constants.KEY_CHAT_COLLECTION).document(chatID).update(constants.KEY_CHAT_LAST_MESSAGE_REFERENCE, docRef)
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
                                                    }
                                                });
                                    }
                                });
                    }
                });

        return response;
    }
}
