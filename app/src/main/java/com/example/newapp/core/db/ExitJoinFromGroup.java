package com.example.newapp.core.db;




import androidx.annotation.NonNull;


import com.example.newapp.core.User;
import com.example.newapp.interfaces.CallbackInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

public class ExitJoinFromGroup {
    CallbackInterface callbackEvent;

    public ExitJoinFromGroup(CallbackInterface callbackEvent) {
        this.callbackEvent = callbackEvent;
    }

    public void joinGroup(FirebaseFirestore fStore, String groupKey) {


        fStore.collection("groups").whereEqualTo("groupKey", groupKey).get()
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

                                batch.update(fStore.collection("users").document(User.getUID()), "GroupKey", groupKey);

                                Map<String, String> userGroupData = new HashMap<>();
                                userGroupData.put("UID", User.getUID());
                                userGroupData.put("Email", User.getEmail());
                                userGroupData.put("Name", User.getName());
                                batch.set(fStore.collection("groups").document(groupKey).collection("groupUsers").document(User.getUID()), userGroupData);

                                batch.update(fStore.collection("groups").document(groupKey), "numberUsers", FieldValue.increment(1));

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

        batch.update(fStore.collection("users").document(User.getUID()), "GroupKey", "");

        batch.delete(fStore.collection("groups").document(User.getUser().getGroupKey()).collection("groupUsers").document(User.getUID()));

        batch.update(fStore.collection("groups").document(User.getUser().getGroupKey()), "numberUsers", FieldValue.increment(-1));

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
