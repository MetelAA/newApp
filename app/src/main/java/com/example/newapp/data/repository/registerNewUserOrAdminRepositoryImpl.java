package com.example.newapp.data.repository;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.newapp.domain.models.registerAdminData;
import com.example.newapp.domain.models.registerUserData;
import com.example.newapp.domain.models.repository.registerNewUserOrAdminRepository;
import com.example.newapp.global.User;
import com.example.newapp.global.constants;
import com.example.newapp.ui.registration.registrationViewModelImpl;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;

public class registerNewUserOrAdminRepositoryImpl implements registerNewUserOrAdminRepository {

    registrationViewModelImpl viewModel;

    public registerNewUserOrAdminRepositoryImpl(registrationViewModelImpl viewModel) {
        this.viewModel = viewModel;
    }

    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();

    @Override
    public void registerUser(registerUserData userData) {


        fAuth.createUserWithEmailAndPassword(userData.email, userData.password)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        viewModel.onError(e.getMessage());
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
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
                                            viewModel.onError(e.getMessage());
                                        }
                                    })
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                viewModel.onUserLogin(true);
                                            } else {
                                                viewModel.onError(task.getException().getMessage());
                                                fAuth.getCurrentUser().delete();
                                            }
                                        }
                                    });
                        } else {
                            viewModel.onError(task.getException().getMessage());
                        }
                    }
                });
    }

    @Override
    public void registerAdmin(registerAdminData adminData) {
        String groupKey = UUID.randomUUID().toString().substring(0, 8);
        Map<String, String> userData = new HashMap<>();

        userData.put(constants.KEY_USER_NAME, adminData.name);
        userData.put(constants.KEY_USER_EMAIL, adminData.email);
        userData.put(constants.KEY_USER_TYPE, adminData.type);
        userData.put(constants.KEY_USER_GROUP_KEY, groupKey);
        userData.put(constants.KEY_USER_STATUS, "");
        fAuth.createUserWithEmailAndPassword(adminData.email, adminData.password)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) { //проверить куда выкидывать будет
                        viewModel.onError(e.getMessage());
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
                            groupData.put(constants.KEY_GROUP_NAME, adminData.groupName);
                            groupData.put(constants.KEY_GROUP_KEY, groupKey);
                            groupData.put(constants.KEY_GROUP_ADMIN, UID);

                            batch.set(fStore.collection(constants.KEY_GROUP_COLLECTION).document(groupKey), groupData);

                            Map<String, String> userGroupData = new HashMap<>();
                            userGroupData.put(constants.KEY_USER_UID, UID);
                            userGroupData.put(constants.KEY_USER_NAME, adminData.name);
                            userGroupData.put(constants.KEY_USER_EMAIL, adminData.email);

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
                                            viewModel.onError(e.getMessage());
                                        }
                                    })
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                User.getUser().create(UID, adminData.name, adminData.email, adminData.type);
                                                User.getUser().setGroupKey(groupKey);
                                                viewModel.onUserLogin(true);
                                            } else {
                                                fAuth.getCurrentUser().delete();
                                                viewModel.onError(task.getException().getMessage());
                                            }
                                        }
                                    });
                        } else {
                            viewModel.onError(task.getException().getMessage());
                        }
                    }
                });
    }
}
