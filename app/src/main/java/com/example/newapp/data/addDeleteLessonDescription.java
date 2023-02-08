package com.example.newapp.data;


import androidx.annotation.NonNull;

import com.example.newapp.global.User;
import com.example.newapp.global.constants;
import com.example.newapp.interfaces.CallbackInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class addDeleteLessonDescription {

    CallbackInterface callbackInterface;

    public addDeleteLessonDescription(CallbackInterface callbackInterface) {
        this.callbackInterface = callbackInterface;
    }


    public void addLesson(FirebaseFirestore fStore, Map<String, String> lessonData){
        fStore.collection(constants.KEY_GROUP_COLLECTION)
                .document(User.getUser().getGroupKey())
                .collection(constants.KEY_GROUP_SCHEDULE_COLLECTION)
                .document(constants.KEY_GROUP_LESSONS_DESCRIPTION_COLLECTION6DOCUMENT)
                .collection(constants.KEY_GROUP_LESSONS_DESCRIPTION_COLLECTION6DOCUMENT)
                .document(lessonData.get(constants.KEY_LESSON_DESCRIPTION_SUBJECT_NAME))
                .set(lessonData)
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
        fStore.collection(constants.KEY_GROUP_COLLECTION)
                .document(User.getUser().getGroupKey())
                .collection(constants.KEY_GROUP_SCHEDULE_COLLECTION)
                .document(constants.KEY_GROUP_LESSONS_DESCRIPTION_COLLECTION6DOCUMENT)
                .collection(constants.KEY_GROUP_LESSONS_DESCRIPTION_COLLECTION6DOCUMENT)
                .document(subjectName).delete()
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
