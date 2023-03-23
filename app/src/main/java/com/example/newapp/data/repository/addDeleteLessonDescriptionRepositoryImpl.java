package com.example.newapp.data.repository;

import androidx.annotation.NonNull;

import com.example.newapp.domain.models.lessonDescription;
import com.example.newapp.domain.models.repository.addDeleteLessonDescriptionRepository;
import com.example.newapp.global.Response;
import com.example.newapp.global.User;
import com.example.newapp.global.constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class addDeleteLessonDescriptionRepositoryImpl implements addDeleteLessonDescriptionRepository {
    FirebaseFirestore fStore;

    public addDeleteLessonDescriptionRepositoryImpl(FirebaseFirestore fStore) {
        this.fStore = fStore;
    }

    @Override
    public Response<lessonDescription, String> addLessonDescription(lessonDescription lessonDescription) {
        Response<lessonDescription, String> response = new Response<>();
        Map<String, String> lessonData = new HashMap<>();
        lessonData.put(constants.KEY_LESSON_DESCRIPTION_SUBJECT_NAME, lessonDescription.subjectName);
        lessonData.put(constants.KEY_LESSON_DESCRIPTION_TEACHER_NAME, lessonDescription.teacherName);

        fStore.collection(constants.KEY_GROUP_COLLECTION)
                .document(User.getUser().getGroupKey())
                .collection(constants.KEY_GROUP_SCHEDULE_COLLECTION)
                .document(constants.KEY_GROUP_LESSONS_DESCRIPTION_COLLECTION6DOCUMENT)
                .collection(constants.KEY_GROUP_LESSONS_DESCRIPTION_COLLECTION6DOCUMENT)
                .document(lessonDescription.subjectName)
                .set(lessonData)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        response.setError(e.getMessage());
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            response.setData(lessonDescription);
                        }else{
                            response.setError(task.getException().getMessage());
                        }
                    }
                });
        return response;
    }

    @Override
    public Response<lessonDescription, String> deleteLessonDescription(lessonDescription lessonDescription) {
        Response<lessonDescription, String> response = new Response<>();

        fStore.collection(constants.KEY_GROUP_COLLECTION)
                .document(User.getUser().getGroupKey())
                .collection(constants.KEY_GROUP_SCHEDULE_COLLECTION)
                .document(constants.KEY_GROUP_LESSONS_DESCRIPTION_COLLECTION6DOCUMENT)
                .collection(constants.KEY_GROUP_LESSONS_DESCRIPTION_COLLECTION6DOCUMENT)
                .document(lessonDescription.subjectName).delete()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        response.setError(e.getMessage());
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            response.setData(lessonDescription);
                        }else{
                            response.setError(task.getException().getMessage());
                        }
                    }
                });

        return response;
    }


}
