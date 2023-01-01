package com.example.newapp.core.db;

import androidx.annotation.NonNull;

import com.example.newapp.core.LessonForShowSchedule;
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

public class getDayLessons {
    CallbackInterfaceWithList callback;

    public getDayLessons(CallbackInterfaceWithList callback) {
        this.callback = callback;
    }

    public void getLessons(FirebaseFirestore fStore, String dayOfWeek){
        fStore.collection("groups").document(User.getUser().getGroupKey())
                .collection("lessonsInformation").document("lessons")
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
                                        docSnapshot.get("numberLesson").toString(),
                                        docSnapshot.get("timeStart").toString(),
                                        docSnapshot.get("timeEnd").toString(),
                                        docSnapshot.get("studyRoom").toString(),
                                        docSnapshot.get("teacher").toString(),
                                        docSnapshot.get("subject").toString()
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
