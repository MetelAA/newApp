package com.example.newapp.core.db;


import android.view.ViewGroup;


import androidx.annotation.NonNull;

import com.example.newapp.core.LessonForScheduleSettings;
import com.example.newapp.core.User;
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

    public void getLessonsFromDB(FirebaseFirestore fStore){
        fStore.collection("groups").document(User.getUser().getGroupKey()).collection("lessonsInformation").document("lessonDescription").collection("lessonDescription").get()
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
                                listLessons.add(new LessonForScheduleSettings(documentSnapshot.get("subjectName").toString(), documentSnapshot.get("teacherName").toString()));
                            }
                            callback.requestResult(listLessons);
                        }else{
                            callback.throwError(task.getException().getMessage());
                        }
                    }
                });
    }

}
