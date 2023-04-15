package com.example.newapp.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.newapp.domain.models.dayEventsData;
import com.example.newapp.domain.models.eventData;
import com.example.newapp.domain.models.repository.getMonthEventsRepository;
import com.example.newapp.global.Response;
import com.example.newapp.global.User;
import com.example.newapp.global.constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class getMonthEventsRepositoryImpl implements getMonthEventsRepository {
    private FirebaseFirestore fStore;

    public getMonthEventsRepositoryImpl(FirebaseFirestore fStore) {
        this.fStore = fStore;
    }

    private int onCompleteCounter = 0;
    private int counter = 0;

    @Override
    public Response<Map<String, dayEventsData>, String> getMonthEvents(LocalDate date) {
        Response<Map<String, dayEventsData>, String> response = new Response<>();
        fStore.collection(constants.KEY_GROUP_COLLECTION)
                .document(User.getUser().getGroupKey())
                .collection(constants.KEY_GROUP_EVENTS_COLLECTION)
                .document(String.valueOf(date.getYear()))
                .collection(String.valueOf(date.getMonthValue()))
                .orderBy("dayDate", Query.Direction.DESCENDING)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        response.setError(e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshots) {

                        Map<String, dayEventsData> responseData = new HashMap<>();
                        onCompleteCounter = querySnapshots.getDocuments().size();
                        counter = 0;
                        if(onCompleteCounter == 0){
                            response.setData(responseData);
                        }
                        for (DocumentSnapshot dayDocument : querySnapshots.getDocuments()) {
                            fStore.collection(constants.KEY_GROUP_COLLECTION)
                                    .document(User.getUser().getGroupKey())
                                    .collection(constants.KEY_GROUP_EVENTS_COLLECTION)
                                    .document(String.valueOf(date.getYear()))
                                    .collection(String.valueOf(date.getMonthValue()))
                                    .document(dayDocument.getId())
                                    .collection(constants.KEY_GROUP_NEWS_MONTH_DAY_EVENTS)
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
                                            ArrayList<eventData> dayEventsList = new ArrayList<>();
                                            for (DocumentSnapshot dayEventsDocument : queryDocumentSnapshots.getDocuments()) {
                                                dayEventsList.add(
                                                        new eventData(
                                                                dayEventsDocument.getString(constants.KEY_GROUP_EVENTS_DAY_EVENT_TITLE),
                                                                dayEventsDocument.getString(constants.KEY_GROUP_EVENTS_DAY_EVENT_DESCRIPTION),
                                                                dayEventsDocument.getString(constants.KEY_GROUP_EVENTS_DAY_EVENT_START_TIME),
                                                                dayEventsDocument.getString(constants.KEY_GROUP_EVENTS_DAY_EVENT_END_TIME)
                                                        )
                                                );
                                            }
                                            responseData.put(dayDocument.getId(), new dayEventsData(dayEventsList, dayDocument.getId()));
                                            counter++;
                                            if(onCompleteCounter == counter) response.setData(responseData);
                                        }
                                    });
                        }
                    }
                });
        return response;
    }
}
