package com.example.newapp.core.db;




import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.newapp.core.User;
import com.example.newapp.core.constants;
import com.example.newapp.interfaces.CallbackInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

public class ExitJoinToGroup {
    CallbackInterface callbackEvent;

    public ExitJoinToGroup(CallbackInterface callbackEvent) {
        this.callbackEvent = callbackEvent;
    }

    public void joinGroup(FirebaseFirestore fStore, String groupKey) {

        fStore.collection(constants.KEY_GROUP_COLLECTION).whereEqualTo(constants.KEY_GROUP_KEY, groupKey).get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbackEvent.throwError(e.getMessage());
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot data = task.getResult();
                            if (!data.isEmpty()) {
                                WriteBatch batch = fStore.batch();

                                batch.update(fStore.collection(constants.KEY_USER_COLLECTION).document(User.getUID()), constants.KEY_USER_GROUP_KEY, groupKey);

                                Map<String, String> userGroupData = new HashMap<>();
                                userGroupData.put(constants.KEY_USER_UID, User.getUID());
                                userGroupData.put(constants.KEY_USER_EMAIL, User.getEmail());
                                userGroupData.put(constants.KEY_USER_NAME, User.getName());

                                batch.set(fStore
                                        .collection(constants.KEY_GROUP_COLLECTION)
                                        .document(groupKey)
                                        .collection(constants.KEY_GROUP_USERS_COLLECTION)
                                        .document(User.getUID()), userGroupData);

                                batch.update(fStore.collection(constants.KEY_GROUP_COLLECTION).document(groupKey), constants.KEY_GROUP_USER_COUNT, FieldValue.increment(1));

                                batch.commit().addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        callbackEvent.throwError(e.getMessage());
                                    }
                                })
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    callbackEvent.requestStatus("ok");
                                                }else{
                                                    callbackEvent.throwError(task.getException().getMessage());
                                                }
                                            }
                                        });
                            } else {
                                callbackEvent.requestStatus("No such group");
                            }
                        } else {
                            callbackEvent.throwError(task.getException().getMessage());
                        }
                    }
                });
    }


    public void exitGroup(FirebaseFirestore fStore) {

        WriteBatch batch = fStore.batch();

        batch.update(fStore.collection(constants.KEY_USER_COLLECTION).document(User.getUID()), constants.KEY_USER_GROUP_KEY, "");

        batch.delete(fStore.collection(constants.KEY_GROUP_COLLECTION).document(User.getUser().getGroupKey()).collection(constants.KEY_GROUP_USERS_COLLECTION).document(User.getUID()));

        batch.update(fStore.collection(constants.KEY_GROUP_COLLECTION).document(User.getUser().getGroupKey()), constants.KEY_GROUP_USER_COUNT, FieldValue.increment(-1));

        batch.commit()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbackEvent.throwError(e.getMessage());
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            callbackEvent.requestStatus("ok");
                        }else{
                            callbackEvent.throwError(task.getException().getMessage());
                        }
                    }
                });
    }
}
