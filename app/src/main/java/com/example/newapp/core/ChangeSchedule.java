package com.example.newapp.core;

import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.newapp.interfaces.CallbackInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangeSchedule {

    CallbackInterface callbackEvent;

    public ChangeSchedule(CallbackInterface callbackEvent) {
        this.callbackEvent = callbackEvent;
    }


    public void changeSchedule(ViewGroup mainElem, FirebaseFirestore fStore, List<Lesson> ListOfLessons, String DayOfWeek) {


        if(ListOfLessons.size() == 0){
            callbackEvent.callback("ok");
            return;
        }
        fStore.collection("groups").document(User.getUser().getGroupKey()).collection("lessons").document(DayOfWeek).collection("dayLessons").get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(mainElem, e.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot snapshot = task.getResult();
                            List<DocumentSnapshot> listData = snapshot.getDocuments();
                            if(!listData.isEmpty()){
                                for (DocumentSnapshot data:listData) {
                                    data.getReference().delete()
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Snackbar.make(mainElem, e.getMessage(), Snackbar.LENGTH_LONG).show();
                                                }
                                            })
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        onChangeSchedule(mainElem, fStore, ListOfLessons, DayOfWeek);
                                                    }else{
                                                        Snackbar.make(mainElem, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                }
                            }else{
                                onChangeSchedule(mainElem, fStore, ListOfLessons, DayOfWeek);
                            }

                        }else{
                            Snackbar.make(mainElem, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void onChangeSchedule(ViewGroup mainElem, FirebaseFirestore fStore, List<Lesson> ListOfLessons, String DayOfWeek) {
        WriteBatch batch = fStore.batch();
        for (Lesson lesson : ListOfLessons) {
            Map<String, String> lessonData = new HashMap<>();
            Log.d("Aboba", lesson.name);
            lessonData.put("lessonName", lesson.name);
            lessonData.put("lessonTime", lesson.time);
            batch.set(fStore.collection("groups").document(User.getUser().getGroupKey()).collection("lessons").document(DayOfWeek).collection("dayLessons")
                    .document(String.valueOf(lesson.number)), lessonData);
        }
        batch.commit()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(mainElem, e.getMessage(), Snackbar.LENGTH_LONG).show();
                        callbackEvent.callback("error");
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            callbackEvent.callback("ok");
                        } else {
                            Snackbar.make(mainElem, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                            callbackEvent.callback("error");
                        }
                    }
                });
    }
}