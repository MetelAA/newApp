package com.example.newapp.data.repository;

import androidx.annotation.NonNull;

import com.example.newapp.domain.models.registerAdminData;
import com.example.newapp.domain.models.registerUserData;
import com.example.newapp.domain.models.repository.registerNewUserOrAdminRepository;
import com.example.newapp.global.Response;
import com.example.newapp.global.User;
import com.example.newapp.global.constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class registerNewUserOrAdminRepositoryImpl implements registerNewUserOrAdminRepository {

    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;

    public registerNewUserOrAdminRepositoryImpl(FirebaseFirestore fStore, FirebaseAuth fAuth) {
        this.fStore = fStore;
        this.fAuth = fAuth;
    }

    @Override
    public Response<String, String> registerUser(registerUserData userData) {
        Response<String, String> response = new Response<>();

        fAuth.createUserWithEmailAndPassword(userData.email, userData.password)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        response.setError(e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        String UID = fAuth.getUid();
                        Map<String, String> data = new HashMap<>();
                        data.put(constants.KEY_USER_NAME, userData.name);
                        data.put(constants.KEY_USER_EMAIL, userData.email);
                        data.put(constants.KEY_USER_TYPE, userData.type);
                        data.put(constants.KEY_USER_GROUP_KEY, "");
                        data.put(constants.KEY_USER_STATUS, "");
                        fStore.collection(constants.KEY_USER_COLLECTION)
                                .document(UID)
                                .set(data)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        fAuth.getCurrentUser().delete();
                                        response.setError(e.getMessage());
                                    }
                                })
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        User.getUser().create(UID, userData.name, userData.email, userData.type);
                                        response.setData("ok");
                                    }
                                });
                    }
                });
        return response;
    }

    @Override
    public Response<String, String> registerAdmin(registerAdminData adminData) {
        Response<String, String> response = new Response<>();

        String groupKey = UUID.randomUUID().toString().substring(0, 8);
        Map<String, String> userData = new HashMap<>();

        userData.put(constants.KEY_USER_NAME, adminData.name);
        userData.put(constants.KEY_USER_EMAIL, adminData.email);
        userData.put(constants.KEY_USER_TYPE, adminData.type);
        userData.put(constants.KEY_USER_GROUP_KEY, groupKey);
        userData.put(constants.KEY_USER_STATUS, constants.KEY_USER_STATUS_EQUALS_ONLINE);
        fAuth.createUserWithEmailAndPassword(adminData.email, adminData.password)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) { //проверить куда выкидывать будет
                        response.setError(e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        String UID = fAuth.getCurrentUser().getUid();
                        WriteBatch batch = fStore.batch();

                        batch.set(fStore.collection(constants.KEY_USER_COLLECTION).document(UID), userData);

                        Map<String, String> groupData = new HashMap<>();
                        groupData.put(constants.KEY_GROUP_NAME, adminData.groupName);
                        groupData.put(constants.KEY_GROUP_KEY, groupKey);
                        groupData.put(constants.KEY_GROUP_ADMIN, UID);

                        batch.set(fStore.collection(constants.KEY_GROUP_COLLECTION).document(groupKey), groupData);

                        Map<String, String> userGroupData = new HashMap<>();
                        userGroupData.put(constants.KEY_USER_UID, UID);
                        userGroupData.put(constants.KEY_USER_NAME, adminData.name);
                        userGroupData.put(constants.KEY_USER_EMAIL, adminData.email);
                        userGroupData.put(constants.KEY_USER_TYPE, adminData.type);

                        batch.set(fStore.collection(constants.KEY_GROUP_COLLECTION)
                                .document(groupKey)
                                .collection(constants.KEY_GROUP_USERS_COLLECTION)
                                .document(UID), userGroupData);

                        Map<String, Object> chatData = new HashMap<>();
                        String chatID = groupKey;
                        chatData.put(constants.KEY_CHAT_CHAT_ID, chatID);
                        chatData.put(constants.KEY_CHAT_MEMBERS_UIDs, Arrays.asList(UID));
                        chatData.put(constants.KEY_CHAT_TYPE, constants.KEY_CHAT_TYPE_EQUALS_GROUP_CHAT);
                        chatData.put(constants.KEY_CHAT_GROUP_CHAT_TITLE, adminData.groupName);
                        batch.set(fStore.collection(constants.KEY_CHAT_COLLECTION).document(chatID), chatData);

                        String messageID = UUID.randomUUID().toString().substring(0, 25);
                        Map<String, Object> chatMessageData = new HashMap<>();
                        chatMessageData.put(constants.KEY_CHAT_MESSAGE_SENDER_NAME, "SYSTEM");
                        chatMessageData.put(constants.KEY_CHAT_MESSAGE_SENDER_UID, " ");
                        chatMessageData.put(constants.KEY_CHAT_MSG_SENT_TIME, new Date());
                        chatMessageData.put(constants.KEY_CHAT_MSG_TYPE, constants.KEY_CHAT_MSG_TYPE_EQUALS_TEXT);
                        chatMessageData.put(constants.KEY_CHAT_MESSAGE_TEXT, "Чат " + adminData.groupName + " создан");
                        DocumentReference documentReference = fStore.collection(constants.KEY_CHAT_COLLECTION).document(chatID).collection(constants.KEY_CHAT_CHAT_MESSAGES_COLLECTION).document(messageID);
                        batch.set(documentReference, chatMessageData);
                        batch.update(fStore.collection(constants.KEY_CHAT_COLLECTION).document(chatID), constants.KEY_CHAT_LAST_MESSAGE_REFERENCE, documentReference);

                        batch.update(fStore.collection(constants.KEY_GROUP_COLLECTION).document(groupKey), constants.KEY_GROUP_USER_COUNT, FieldValue.increment(1));

                        batch.commit()
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        fAuth.getCurrentUser().delete();
                                        response.setError(e.getMessage());
                                    }
                                })
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            User.getUser().create(UID, adminData.name, adminData.email, adminData.type);
                                            User.getUser().setGroupKey(groupKey);
                                            response.setData("ok");
                                        } else {
                                            fAuth.getCurrentUser().delete();
                                            response.setError(task.getException().getMessage());
                                        }
                                    }
                                });
                    }
                });
        return response;
    }
}
