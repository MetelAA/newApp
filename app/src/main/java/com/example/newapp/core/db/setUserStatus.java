package com.example.newapp.core.db;

import androidx.annotation.NonNull;

import com.example.newapp.core.User;
import com.example.newapp.core.constants;
import com.example.newapp.interfaces.CallbackInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

public class setUserStatus {

    CallbackInterface callback;


    public setUserStatus(CallbackInterface callback) {
        this.callback = callback;
    }


    public void setStatus(FirebaseFirestore fStore, String status){
        fStore.collection(constants.KEY_USER_COLLECTION).document(User.getUID()).update(constants.KEY_USER_STATUS, status)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.throwError(e.getMessage());
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            callback.requestStatus("ok");
                        }else{
                            callback.throwError(task.getException().getMessage());
                        }
                    }
                });
    }

}

