package com.example.newapp.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.newapp.domain.models.arrayListForSchedule;
import com.example.newapp.domain.models.lesson;
import com.example.newapp.domain.models.repository.getDayLessonsRepository;
import com.example.newapp.global.Response;
import com.example.newapp.global.User;
import com.example.newapp.global.constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class getDayLessonsRepositoryImpl implements getDayLessonsRepository {
    FirebaseFirestore fStore;
    public getDayLessonsRepositoryImpl(FirebaseFirestore fStore) {
        this.fStore = fStore;
    }
    @Override
    public Response<arrayListForSchedule, String> getDayLessons(String dayOfWeek) {
        Log.d("Aboba", "getDayLessons");
        Response<arrayListForSchedule, String> response = new Response<>();

        fStore.collection(constants.KEY_GROUP_COLLECTION)
                .document(User.getUser().getGroupKey())
                .collection(constants.KEY_GROUP_SCHEDULE_COLLECTION)
                .document(constants.KEY_GROUP_DAY_OF_WEEK_SCHEDULE_DOCUMENT)
                .collection(dayOfWeek).get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       response.setError(e.getMessage());
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            arrayListForSchedule resultList = new arrayListForSchedule();
                            QuerySnapshot querySnapshot = task.getResult();
                            List<DocumentSnapshot> listSnapshot = querySnapshot.getDocuments();

                            if(listSnapshot.isEmpty()){
                                resultList.setDayOfWeek(dayOfWeek);
                            }else{
                                resultList.setDayOfWeek(dayOfWeek);
                                for (DocumentSnapshot docSnapshot:listSnapshot) {
                                resultList.add(new lesson(
                                        docSnapshot.get(constants.KEY_LESSON_NUMBER_LESSON).toString(),
                                        docSnapshot.get(constants.KEY_LESSON_START_TIME).toString(),
                                        docSnapshot.get(constants.KEY_LESSON_END_TIME).toString(),
                                        docSnapshot.get(constants.KEY_LESSON_STUDY_ROOM).toString(),
                                        docSnapshot.get(constants.KEY_LESSON_DESCRIPTION_TEACHER_NAME).toString(),
                                        docSnapshot.get(constants.KEY_LESSON_DESCRIPTION_SUBJECT_NAME).toString()
                                ));
                                }
                            }
                            Log.d("Aboba", "response setData  result List по дню - " + resultList.getDayOfWeek() + "  стастус нахуй статус"  + "  со знач "  + resultList.toString());
                            response.setData(resultList);
                        }else{
                            response.setError(task.getException().getMessage());
                        }
                    }
                });
        return response;
    }
}
