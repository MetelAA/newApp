package com.example.newapp.data;


import androidx.annotation.NonNull;

import com.example.newapp.domain.models.oldModels.LessonForScheduleSettings;
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

public class getLessonDescription {
    CallbackInterfaceWithList callback;

    public getLessonDescription(CallbackInterfaceWithList callback) {
        this.callback = callback;
    }

    public void getLessonsDescriptionFromDB(FirebaseFirestore fStore){
        fStore.collection(constants.KEY_GROUP_COLLECTION).document(User.getUser().getGroupKey())
                .collection(constants.KEY_GROUP_SCHEDULE_COLLECTION)
                .document(constants.KEY_GROUP_LESSONS_DESCRIPTION_COLLECTION6DOCUMENT)
                .collection(constants.KEY_GROUP_LESSONS_DESCRIPTION_COLLECTION6DOCUMENT)
                .get()
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
                            QuerySnapshot Querysnapshot = task.getResult();
                            List<DocumentSnapshot> listData = Querysnapshot.getDocuments();
                            ArrayList<LessonForScheduleSettings> listLessons = new ArrayList<>();
                            for (DocumentSnapshot documentSnapshot:listData) {
                                listLessons.add(new LessonForScheduleSettings(
                                        documentSnapshot.get(constants.KEY_LESSON_DESCRIPTION_SUBJECT_NAME).toString(),
                                        documentSnapshot.get(constants.KEY_LESSON_DESCRIPTION_TEACHER_NAME).toString())
                                );
                            }
                            callback.requestResult(listLessons);
                        }else{
                            callback.throwError(task.getException().getMessage());
                        }
                    }
                });
    }

}
