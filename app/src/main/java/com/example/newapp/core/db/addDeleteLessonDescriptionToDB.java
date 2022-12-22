package com.example.newapp.core.db;


import androidx.annotation.NonNull;

import com.example.newapp.core.User;
import com.example.newapp.interfaces.CallbackInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class addDeleteLessonDescriptionToDB {

    CallbackInterface callbackInterface;

    public addDeleteLessonDescriptionToDB(CallbackInterface callbackInterface) {
        this.callbackInterface = callbackInterface;
    }


    public void addLesson(FirebaseFirestore fStore, Map<String, String> lessonData){
        fStore.collection("groups").document(User.getUser().getGroupKey()).collection("lessonsInformation").document("lessonDescription").collection("lessonDescription").document(lessonData.get("subjectName")).set(lessonData)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbackInterface.throwError(e.getMessage());
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            callbackInterface.requestStatus("ok");
                        }else{
                           callbackInterface.throwError(task.getException().getMessage());
                        }
                    }
                });
    }

    public void deleteLesson(FirebaseFirestore fStore, String subjectName){
        fStore.collection("groups").document(User.getUser().getGroupKey()).collection("lessonsInformation").document("lessonDescription").collection("lessonDescription").document(subjectName).delete()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbackInterface.throwError(e.getMessage());
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            callbackInterface.requestStatus("ok");
                        }else{
                            callbackInterface.throwError(task.getException().getMessage());
                        }
                    }
                });


    }


}
