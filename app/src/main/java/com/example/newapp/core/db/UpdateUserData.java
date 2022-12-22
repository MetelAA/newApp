package com.example.newapp.core.db;


import android.text.TextUtils;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.newapp.core.User;
import com.example.newapp.interfaces.CallbackInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateUserData{
    CallbackInterface callbackEvent;


    public UpdateUserData(CallbackInterface callbackEvent) {
        this.callbackEvent = callbackEvent;
    }

    public void updateUserData(FirebaseAuth fAuth, FirebaseFirestore fStore){
            if(fAuth.getUid() != null){
                String UID = fAuth.getUid();

                fStore.collection("users").document(UID).get()
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                callbackEvent.throwError(e.getMessage());
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
                                                        callbackEvent.throwError(e.getMessage());
                                                    }
                                                })
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if(task.isSuccessful()){
                                                            DocumentSnapshot groupData = task.getResult();
                                                            User.getUser().setGroupName(groupData.get("nameGroup").toString());
                                                            User.getUser().setNumberUsers((long) groupData.get("numberUsers"));
                                                            callbackEvent.requestStatus("ok");
                                                        }else{
                                                            callbackEvent.throwError(task.getException().getMessage());
                                                        }

                                                    }
                                                });
                                    }else{
                                        User.getUser().setGroupName("");
                                        callbackEvent.requestStatus("ok");
                                    }
                                }else{
                                    callbackEvent.throwError(task.getException().getMessage());
                                }
                            }
                        });
            }else {
                //расцениваю как ошибку так как юзер который не вошёл вообще это видеть не должен
                callbackEvent.throwError("Not sigh up");
            }
    }


}
