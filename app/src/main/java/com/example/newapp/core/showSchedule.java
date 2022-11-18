package com.example.newapp.core;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.example.newapp.adapters.customListLessonsArrayAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.List;

public class showSchedule { //не очень правильно, но разводить размазню на 10 классов я не хочу , тк потребление памяти будет ебейшее  и я ксати живой пока что => принцип единственной ответственности идйт нахуй :)
    public void showSchedule(ViewGroup mainElem, FirebaseFirestore fStore, ListView listView, Context context, String DayOfWeek) {

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
                        if (task.isSuccessful()) {
                            QuerySnapshot snapshot = task.getResult();
                            List<DocumentSnapshot> listData = snapshot.getDocuments();
                            int position = 1;
                            ArrayList<Lesson> listLessons = new ArrayList<>();
                            for (DocumentSnapshot data : listData) {
                                listLessons.add(new Lesson(data.get("lessonName").toString(), data.get("lessonTime").toString(), position));
                                position++;
                            }
                            customListLessonsArrayAdapter customListLessonsArrayAdapter = new customListLessonsArrayAdapter(context, listLessons);
                            listView.setClickable(false);
                            listView.setAdapter(customListLessonsArrayAdapter);
                        } else {
                            Snackbar.make(mainElem, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();

                        }
                    }
                });


    }
}