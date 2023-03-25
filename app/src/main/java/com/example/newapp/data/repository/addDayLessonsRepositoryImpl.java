package com.example.newapp.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.newapp.domain.models.lesson;
import com.example.newapp.domain.models.oldModels.LessonForScheduleSettings;
import com.example.newapp.domain.models.repository.addDayLessonsRepository;
import com.example.newapp.global.Response;
import com.example.newapp.global.User;
import com.example.newapp.global.constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class addDayLessonsRepositoryImpl implements addDayLessonsRepository {
    FirebaseFirestore fStore;
    public addDayLessonsRepositoryImpl(FirebaseFirestore fStore) {
        this.fStore = fStore;
    }

    @Override
    public Response<String, String> addDayLessons(ArrayList<lesson> listLessons, String dayOfWeek) {
        Response<String, String> response = new Response<>();

        WriteBatch batch = fStore.batch();
        for (int i = 1; i <= 10; i++) {
            batch.delete(fStore.collection(constants.KEY_GROUP_COLLECTION)
                    .document(User.getUser().getGroupKey())
                    .collection(constants.KEY_GROUP_SCHEDULE_COLLECTION)
                    .document(constants.KEY_GROUP_DAY_OF_WEEK_SCHEDULE_DOCUMENT)
                    .collection(dayOfWeek)
                    .document(String.valueOf(i)));
        }

        for (lesson lesson : listLessons) {
            Map<String, String> lessonMap = new HashMap<>();
            lessonMap.put(constants.KEY_LESSON_NUMBER_LESSON, lesson.numberLesson);
            lessonMap.put(constants.KEY_LESSON_START_TIME, lesson.timeStart);
            lessonMap.put(constants.KEY_LESSON_END_TIME, lesson.timeEnd);
            lessonMap.put(constants.KEY_LESSON_STUDY_ROOM, lesson.studyRoom);
            lessonMap.put(constants.KEY_LESSON_DESCRIPTION_TEACHER_NAME, lesson.teacher);
            lessonMap.put(constants.KEY_LESSON_DESCRIPTION_SUBJECT_NAME, lesson.subject);
            Log.d("Aboba", "AddDayLessons map - " + lessonMap.toString() + " USer getGroupKey " + User.getUser().getGroupKey());
            batch.set(fStore.collection(constants.KEY_GROUP_COLLECTION)
                    .document(User.getUser().getGroupKey())
                    .collection(constants.KEY_GROUP_SCHEDULE_COLLECTION)
                    .document(constants.KEY_GROUP_DAY_OF_WEEK_SCHEDULE_DOCUMENT)
                    .collection(dayOfWeek)
                    .document(lesson.numberLesson), lessonMap);
        }

        batch.commit()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        response.setError(e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        response.setData("ok");
                    }
                });
        return response;
    }
}
