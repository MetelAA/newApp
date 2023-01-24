package com.example.newapp.core.db;


import androidx.annotation.NonNull;

import com.example.newapp.core.constants;
import com.example.newapp.interfaces.CallbackInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import com.example.newapp.core.User;

import java.util.List;
import java.util.Map;

public class addDayLessons {
    CallbackInterface callbackInterface;

    public addDayLessons(CallbackInterface callbackInterface) {
        this.callbackInterface = callbackInterface;
    }

    public void addLessons(FirebaseFirestore fStore, List<Map<String, String>> listLessons, String dayOfWeek){
        WriteBatch batch = fStore.batch();

        for (int i = 1; i <= 10; i++) {
            batch.delete(fStore.collection(constants.KEY_GROUP_COLLECTION)
                    .document(User.getUser().getGroupKey())
                    .collection(constants.KEY_GROUP_SCHEDULE_COLLECTION)
                    .document(constants.KEY_GROUP_DAY_OF_WEEK_SCHEDULE_DOCUMENT)
                    .collection(dayOfWeek)
                    .document(String.valueOf(i)));
        }


        for (Map<String, String> lesson:listLessons) {
            batch.set(fStore.collection(constants.KEY_GROUP_COLLECTION)
                    .document(User.getUser().getGroupKey())
                    .collection(constants.KEY_GROUP_SCHEDULE_COLLECTION)
                    .document(constants.KEY_GROUP_DAY_OF_WEEK_SCHEDULE_DOCUMENT)
                    .collection(dayOfWeek)
                    .document(lesson.get(constants.KEY_LESSON_NUMBER_LESSONS)), lesson);
        }
        batch.commit()
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
