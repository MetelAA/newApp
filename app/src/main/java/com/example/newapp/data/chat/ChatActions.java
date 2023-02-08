package com.example.newapp.data.chat;

import androidx.annotation.NonNull;

import com.example.newapp.global.constants;
import com.example.newapp.interfaces.CallbackInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Map;

public class ChatActions {
    CallbackInterface callback;

    public ChatActions(CallbackInterface callback) {
        this.callback = callback;
    }


    public void SendMessage(FirebaseFirestore fStore){

    }



    public void createNewChat(FirebaseFirestore fStore, Map<String, Object> chatData, CallbackInterface localCallback){
        fStore.collection(constants.KEY_CHAT_COLLECTION).add(chatData)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        localCallback.throwError(e.getMessage());
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            localCallback.requestStatus("ok");
                        }else{
                            localCallback.throwError(task.getException().getMessage());
                        }
                    }
                });
    }
}
