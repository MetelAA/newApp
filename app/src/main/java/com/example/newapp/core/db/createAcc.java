package com.example.newapp.core.db;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.newapp.MainActivity;
import com.example.newapp.core.User;
import com.example.newapp.core.constants;
import com.example.newapp.interfaces.CallbackInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class createAcc {
    CallbackInterface callback;

    public createAcc(CallbackInterface callback) {
        this.callback = callback;
    }

    public void createAdminAcc(FirebaseAuth fAuth, FirebaseFirestore fStore, String Name, String Password, String Email, String Type, String groupName){
        String groupKey = UUID.randomUUID().toString().substring(0, 8);
        Map<String, String> userData = new HashMap<>();

        userData.put(constants.KEY_USER_NAME, Name);
        userData.put(constants.KEY_USER_EMAIL, Email);
        userData.put(constants.KEY_USER_TYPE, Type);
        userData.put(constants.KEY_USER_GROUP_KEY, groupKey);
        userData.put(constants.KEY_USER_STATUS, "");
        fAuth.createUserWithEmailAndPassword(Email, Password)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) { //проверить куда выкидывать будет
                        callback.throwError(e.getMessage());
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String UID = fAuth.getCurrentUser().getUid();
                            WriteBatch batch = fStore.batch();

                            batch.set(fStore.collection(constants.KEY_USER_COLLECTION).document(UID), userData);

                            Map<String, String> groupData = new HashMap<>();
                            groupData.put(constants.KEY_GROUP_NAME, groupName);
                            groupData.put(constants.KEY_GROUP_KEY, groupKey);
                            groupData.put(constants.KEY_GROUP_ADMIN, UID);

                            batch.set(fStore.collection(constants.KEY_GROUP_COLLECTION).document(groupKey), groupData);

                            Map<String, String> userGroupData = new HashMap<>();
                            userGroupData.put(constants.KEY_USER_UID, UID);
                            userGroupData.put(constants.KEY_USER_NAME, Name);
                            userGroupData.put(constants.KEY_USER_EMAIL, Email);

                            batch.set(fStore.collection(constants.KEY_GROUP_COLLECTION)
                                    .document(groupKey)
                                    .collection(constants.KEY_GROUP_USERS_COLLECTION)
                                    .document(UID), userGroupData);

                            batch.update(fStore.collection(constants.KEY_GROUP_COLLECTION).document(groupKey), constants.KEY_GROUP_USER_COUNT, FieldValue.increment(1));

                            batch.commit()
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            fAuth.getCurrentUser().delete();
                                            callback.throwError(e.getMessage());
                                        }
                                    })
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                User.getUser().create(UID, Name, Email, Type);
                                                User.getUser().setGroupKey(groupKey);
                                                User.getUser().setGroupName(groupName);
                                                callback.requestStatus("ok");
                                            } else {
                                                fAuth.getCurrentUser().delete();
                                                callback.throwError(task.getException().getMessage());
                                            }
                                        }
                                    });
                        } else {
                            callback.throwError(task.getException().getMessage());
                        }
                    }
                });
    }

    public void createUserProfile(FirebaseAuth fAuth, FirebaseFirestore fStore, String Name, String Password, String Email, String Type){
        fAuth.createUserWithEmailAndPassword(Email, Password)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.throwError(e.getMessage());
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String UID = fAuth.getCurrentUser().getUid(); //не уверен что именно current (проверить)
                            Map<String, String> data = new HashMap<>();
                            data.put(constants.KEY_USER_NAME, Name);
                            data.put(constants.KEY_USER_EMAIL, Email);
                            data.put(constants.KEY_USER_TYPE, Type);
                            data.put(constants.KEY_USER_GROUP_KEY, "");
                            data.put(constants.KEY_USER_STATUS, "");

                            fStore.collection(constants.KEY_USER_COLLECTION).document(UID).set(data)

                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            callback.throwError(e.getMessage());
                                        }
                                    })

                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                User.getUser().create(UID, Name, Email, Type);
                                                User.getUser().setGroupKey("");
                                                User.getUser().setGroupName("");
                                                callback.requestStatus("ok");
                                            } else {
                                                fAuth.getCurrentUser().delete();
                                                callback.throwError(task.getException().getMessage());
                                            }
                                        }
                                    });
                        } else {
                            callback.throwError(task.getException().getMessage());
                        }
                    }
                });
    }


}
