package com.example.newapp.data;

import androidx.annotation.NonNull;

import com.example.newapp.domain.models.oldModels.LessonForShowSchedule;
import com.example.newapp.global.User;
import com.example.newapp.global.constants;
import com.example.newapp.interfaces.CallbackInterfaceWithList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class getDayLessons {
    CallbackInterfaceWithList callback;

    public getDayLessons(CallbackInterfaceWithList callback) {
        this.callback = callback;
    }

    public void getLessons(FirebaseFirestore fStore, String dayOfWeek){
        fStore.collection(constants.KEY_GROUP_COLLECTION)
                .document(User.getUser().getGroupKey())
                .collection(constants.KEY_GROUP_SCHEDULE_COLLECTION)
                .document(constants.KEY_GROUP_DAY_OF_WEEK_SCHEDULE_DOCUMENT)
                .collection(dayOfWeek).get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.throwError(e.getMessage());
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            ArrayList<LessonForShowSchedule> resultList = new ArrayList<>();

                            QuerySnapshot querySnapshot = task.getResult();
                            List<DocumentSnapshot> listSnapshot = querySnapshot.getDocuments();
                            for (DocumentSnapshot docSnapshot:listSnapshot) {
                                resultList.add(new LessonForShowSchedule(
                                        docSnapshot.get(constants.KEY_LESSON_NUMBER_LESSONS).toString(),
                                        docSnapshot.get(constants.KEY_LESSON_START_TIME).toString(),
                                        docSnapshot.get(constants.KEY_LESSON_END_TIME).toString(),
                                        docSnapshot.get(constants.KEY_LESSON_STUDY_ROOM).toString(),
                                        docSnapshot.get(constants.KEY_LESSON_DESCRIPTION_TEACHER_NAME).toString(),
                                        docSnapshot.get(constants.KEY_LESSON_DESCRIPTION_SUBJECT_NAME).toString()
                                ));
                            }
                            callback.requestResult(resultList);
                        }else{
                            callback.throwError(task.getException().getMessage());
                        }
                    }
                });
    }
}
