package com.example.newapp.core.db;


import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.example.newapp.core.User;
import com.example.newapp.core.constants;
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

                fStore.collection(constants.KEY_USER_COLLECTION).document(UID).get()
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

                                    User.getUser().create(
                                            UID,
                                            userData.get(constants.KEY_USER_NAME).toString(),
                                            userData.get(constants.KEY_USER_EMAIL).toString(),
                                            userData.get(constants.KEY_USER_TYPE).toString()
                                    );
                                    String GroupKey = userData.get(constants.KEY_USER_GROUP_KEY).toString();
                                    User.getUser().setGroupKey(GroupKey);

                                    if(!(TextUtils.equals(GroupKey, ""))){
                                        fStore.collection(constants.KEY_GROUP_COLLECTION).document(GroupKey).get()
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
                                                            User.getUser().setGroupName(groupData.get(constants.KEY_GROUP_NAME).toString());
                                                            User.getUser().setNumberUsers((long) groupData.get(constants.KEY_GROUP_USER_COUNT));
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
