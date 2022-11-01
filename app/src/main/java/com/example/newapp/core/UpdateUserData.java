package com.example.newapp.core;


import android.text.TextUtils;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.newapp.interfaces.CallbackInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateUserData{
    CallbackInterface callbackEvent;


    public UpdateUserData(CallbackInterface callbackEvent) {
        this.callbackEvent = callbackEvent;
    }

    public void updateUserData(ViewGroup mainElem, FirebaseAuth fAuth, FirebaseFirestore fStore){
            final Boolean[] status = {false};
            if(fAuth.getUid() != null){
                String UID = fAuth.getUid();

                fStore.collection("users").document(UID).get()
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //обрабтать ошибку
                                Snackbar.make(mainElem, e.getMessage(), Snackbar.LENGTH_LONG);
                                callbackEvent.callback(status[0]);
                            }
                        })
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    DocumentSnapshot userData = task.getResult();
                                    User.getUser().create(UID, userData.get("Name").toString(), userData.get("Email").toString(), userData.get("Type").toString());
                                    User.getUser().setGroupKey(userData.get("GroupKey").toString());
                                    if(!(TextUtils.equals(userData.get("GroupKey").toString(), ""))){
                                        fStore.collection("groups").document(userData.get("GroupKey").toString()).get()
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Snackbar.make(mainElem, e.getMessage(), Snackbar.LENGTH_LONG).show();
                                                        callbackEvent.callback(status[0]);
                                                    }
                                                })
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if(task.isSuccessful()){
                                                            DocumentSnapshot groupData = task.getResult();
                                                            User.getUser().setGroupName(groupData.get("nameGroup").toString());
                                                            status[0] = true;
                                                            callbackEvent.callback(status[0]);
                                                        }else{
                                                            //отрабатывать ошибку
                                                            Snackbar.make(mainElem, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                                            callbackEvent.callback(status[0]);
                                                        }
                                                    }
                                                });
                                    }else{
                                        User.getUser().setGroupName("");
                                        status[0] = true;
                                        callbackEvent.callback(status[0]);
                                    }
                                }else{
                                    Snackbar.make(mainElem, task.getException().getMessage(), Snackbar.LENGTH_LONG);
                                    callbackEvent.callback(status[0]);
                                }
                            }
                        });
            }else {
                callbackEvent.callback(status[0]);
            }
    }


}
