package com.example.newapp.data.repository;

import androidx.annotation.NonNull;

import com.example.newapp.domain.models.eventData;
import com.example.newapp.domain.models.repository.addNewDayEventRepository;
import com.example.newapp.global.Response;
import com.example.newapp.global.User;
import com.example.newapp.global.constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class addNewDayEventRepositoryImpl implements addNewDayEventRepository {
    FirebaseFirestore fStore;

    public addNewDayEventRepositoryImpl(FirebaseFirestore fStore) {
        this.fStore = fStore;
    }

    @Override
    public Response<eventData, String> addNewDauEvent(eventData data, LocalDate date) {
        Response<eventData, String> response = new Response<>();

        Map<String, Object> dayData = new HashMap<>();
        dayData.put(constants.KEY_GROUP_EVENTS_DAY_DATE, date.getDayOfMonth());
        fStore.collection(constants.KEY_GROUP_COLLECTION)
                .document(User.getUser().getGroupKey())
                .collection(constants.KEY_GROUP_EVENTS_COLLECTION)
                .document(String.valueOf(date.getYear()))
                .collection(String.valueOf(date.getMonthValue()))
                .document(String.valueOf(date.getDayOfMonth()))
                .set(dayData)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        response.setError(e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Map<String, Object> eventData = new HashMap<>();
                        eventData.put(constants.KEY_GROUP_EVENTS_DAY_EVENT_TITLE, data.eventTitle);
                        eventData.put(constants.KEY_GROUP_EVENTS_DAY_EVENT_DESCRIPTION, data.eventDescription);
                        if(data.isFullDay){
                            eventData.put(constants.KEY_GROUP_EVENTS_DAY_EVENT_IS_FULL_DAY, true);
                        }else{
                            eventData.put(constants.KEY_GROUP_EVENTS_DAY_EVENT_IS_FULL_DAY, false);
                            eventData.put(constants.KEY_GROUP_EVENTS_DAY_EVENT_START_TIME, data.eventStartTime);
                            eventData.put(constants.KEY_GROUP_EVENTS_DAY_EVENT_END_TIME, data.eventEndTime);
                        }
                        fStore.collection(constants.KEY_GROUP_COLLECTION)
                                .document(User.getUser().getGroupKey())
                                .collection(constants.KEY_GROUP_EVENTS_COLLECTION)
                                .document(String.valueOf(date.getYear()))
                                .collection(String.valueOf(date.getMonthValue()))
                                .document(String.valueOf(date.getDayOfMonth()))
                                .collection(constants.KEY_GROUP_NEWS_MONTH_DAY_EVENTS)
                                .add(eventData)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        response.setError(e.getMessage());
                                    }
                                })
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        response.setData(data);
                                    }
                                });
                    }
                });
        return response;
    }
}
