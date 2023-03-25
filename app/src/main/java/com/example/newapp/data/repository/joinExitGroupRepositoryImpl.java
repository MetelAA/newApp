package com.example.newapp.data.repository;

import androidx.annotation.NonNull;

import com.example.newapp.domain.models.repository.joinExitGroupRepository;
import com.example.newapp.global.Response;
import com.example.newapp.global.User;
import com.example.newapp.global.constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;

public class joinExitGroupRepositoryImpl implements joinExitGroupRepository {
    FirebaseFirestore fStore;

    public joinExitGroupRepositoryImpl(FirebaseFirestore fStore) {
        this.fStore = fStore;
    }
    @Override
    public Response<String, String> joinGroup(String groupKey) {
        Response<String, String> response = new Response<>();

        fStore.collection(constants.KEY_GROUP_COLLECTION).whereEqualTo(constants.KEY_GROUP_KEY, groupKey).get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        response.setError(e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            WriteBatch batch = fStore.batch();

                            batch.update(fStore.collection(constants.KEY_USER_COLLECTION).document(User.getUID()), constants.KEY_USER_GROUP_KEY, groupKey);

                            Map<String, String> userGroupData = new HashMap<>();
                            userGroupData.put(constants.KEY_USER_UID, User.getUID());
                            userGroupData.put(constants.KEY_USER_EMAIL, User.getEmail());
                            userGroupData.put(constants.KEY_USER_NAME, User.getName());
                            userGroupData.put(constants.KEY_USER_TYPE, User.getType());

                            batch.set(fStore
                                    .collection(constants.KEY_GROUP_COLLECTION)
                                    .document(groupKey)
                                    .collection(constants.KEY_GROUP_USERS_COLLECTION)
                                    .document(User.getUID()), userGroupData);

                            batch.update(fStore.collection(constants.KEY_GROUP_COLLECTION).document(groupKey), constants.KEY_GROUP_USER_COUNT, FieldValue.increment(1));

                            batch.commit().addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            response.setError(e.getMessage());
                                        }
                                    })
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            User.getUser().setGroupKey(groupKey);
                                            response.setData("ok");
                                        }
                                    });
                        } else {
                            response.setError("No such group");
                        }
                    }
                });

        return response;
    }

    @Override
    public Response<String, String> exitGroup() {
        Response<String, String> response = new Response<>();

        WriteBatch batch = fStore.batch();

        batch.update(fStore.collection(constants.KEY_USER_COLLECTION).document(User.getUID()), constants.KEY_USER_GROUP_KEY, "");

        batch.delete(fStore.collection(constants.KEY_GROUP_COLLECTION).document(User.getUser().getGroupKey()).collection(constants.KEY_GROUP_USERS_COLLECTION).document(User.getUID()));

        batch.update(fStore.collection(constants.KEY_GROUP_COLLECTION).document(User.getUser().getGroupKey()), constants.KEY_GROUP_USER_COUNT, FieldValue.increment(-1));

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
                        User.getUser().setGroupKey(null);
                        response.setData("ok");
                    }
                });

        return response;
    }
}
