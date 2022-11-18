package com.example.newapp.core;


import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.newapp.interfaces.CallbackInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
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

    public void joinGroup(ViewGroup mainElem, FirebaseFirestore fStore, String groupKey) {
        final String[] status = {"error"};

        fStore.collection("groups").whereEqualTo("groupKey", groupKey).get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(mainElem, e.getMessage(), Snackbar.LENGTH_LONG).show();
                        callbackEvent.callback(status[0]);
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
                                        Snackbar.make(mainElem, e.getMessage(), Snackbar.LENGTH_LONG).show();
                                        callbackEvent.callback(status[0]);
                                    }
                                })
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    status[0] = "ok";
                                                }else{
                                                    Snackbar.make(mainElem, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                                }
                                                callbackEvent.callback(status[0]);
                                            }
                                        });
                            } else {
                                status[0] = "no such group";
                                callbackEvent.callback(status[0]);
                            }
                        } else {
                            Snackbar.make(mainElem, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                            callbackEvent.callback(status[0]);
                        }
                    }
                });
    }


    public void exitGroup(ViewGroup mainElem, FirebaseFirestore fStore) {
        final String[] status = {"error"};

        WriteBatch batch = fStore.batch();

        batch.update(fStore.collection("users").document(User.getUID()), "GroupKey", "");

        batch.delete(fStore.collection("groups").document(User.getUser().getGroupKey()).collection("groupUsers").document(User.getUID()));

        batch.update(fStore.collection("groups").document(User.getUser().getGroupKey()), "numberUsers", FieldValue.increment(-1));

        batch.commit()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(mainElem, e.getMessage(), Snackbar.LENGTH_LONG).show();
                        callbackEvent.callback(status[0]);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            status[0] = "ok";
                        }else{
                            Snackbar.make(mainElem, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                        callbackEvent.callback(status[0]);
                    }
                });
    }
}
