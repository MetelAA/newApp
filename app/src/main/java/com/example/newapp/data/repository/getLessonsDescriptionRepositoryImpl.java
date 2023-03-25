package com.example.newapp.data.repository;

import androidx.annotation.NonNull;

import com.example.newapp.domain.models.lessonDescription;
import com.example.newapp.domain.models.oldModels.LessonForScheduleSettings;
import com.example.newapp.domain.models.repository.getLessonsDescriptionRepository;
import com.example.newapp.global.Response;
import com.example.newapp.global.User;
import com.example.newapp.global.constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class getLessonsDescriptionRepositoryImpl implements getLessonsDescriptionRepository {
    FirebaseFirestore fStore;
    public getLessonsDescriptionRepositoryImpl(FirebaseFirestore fStore) {
        this.fStore = fStore;
    }
    @Override
    public Response<ArrayList<lessonDescription>, String> getLessonsDescription() {
        Response<ArrayList<lessonDescription>, String> response = new Response<>();
        fStore.collection(constants.KEY_GROUP_COLLECTION).document(User.getUser().getGroupKey())
                .collection(constants.KEY_GROUP_SCHEDULE_COLLECTION)
                .document(constants.KEY_GROUP_LESSONS_DESCRIPTION_COLLECTION6DOCUMENT)
                .collection(constants.KEY_GROUP_LESSONS_DESCRIPTION_COLLECTION6DOCUMENT)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        response.setError(e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> listData = queryDocumentSnapshots.getDocuments();
                        ArrayList<lessonDescription> listLessonsDescription = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot:listData) {
                            listLessonsDescription.add(new lessonDescription(
                                    documentSnapshot.get(constants.KEY_LESSON_DESCRIPTION_SUBJECT_NAME).toString(),
                                    documentSnapshot.get(constants.KEY_LESSON_DESCRIPTION_TEACHER_NAME).toString())
                            );
                        }
                        response.setData(listLessonsDescription);
                    }
                });
        return response;
    }
}
