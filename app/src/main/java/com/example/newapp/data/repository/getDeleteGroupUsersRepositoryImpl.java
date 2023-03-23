package com.example.newapp.data.repository;

import androidx.annotation.NonNull;

import com.example.newapp.domain.models.groupUserData;
import com.example.newapp.domain.models.oldModels.GroupUser;
import com.example.newapp.domain.models.repository.getDeleteGroupUsersRepository;
import com.example.newapp.global.Response;
import com.example.newapp.global.User;
import com.example.newapp.global.constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class getDeleteGroupUsersRepositoryImpl implements getDeleteGroupUsersRepository {
    FirebaseFirestore fStore;

    public getDeleteGroupUsersRepositoryImpl(FirebaseFirestore fStore) {
        this.fStore = fStore;
    }

    @Override
    public Response<ArrayList<groupUserData>, String> getUsersList() {
        Response<ArrayList<groupUserData>, String> response = new Response<>();

        fStore.collection(constants.KEY_GROUP_COLLECTION).document(User.getUser().getGroupKey()).collection(constants.KEY_GROUP_USERS_COLLECTION).get()
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
                            ArrayList<groupUserData> listUsers = new ArrayList<>();
                            QuerySnapshot queryDocumentSnapshots = task.getResult();
                            List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot document:documents) {
                                listUsers.add(new groupUserData(
                                        document.get(constants.KEY_USER_UID).toString(),
                                        document.get(constants.KEY_USER_NAME).toString(),
                                        document.get(constants.KEY_USER_EMAIL).toString(),
                                        document.get(constants.KEY_USER_TYPE).toString()
                                    )
                                );
                            }
                            response.setData(listUsers);
                        }else{
                            response.setError(task.getException().getMessage());
                        }
                    }
                });

        return response;
    }

    @Override
    public Response<String, String> deleteUser(String deleteUserUID) {
        Response<String, String> response = new Response<>();
        WriteBatch batch = fStore.batch();
        batch.delete(fStore.collection(constants.KEY_GROUP_COLLECTION).document(User.getUser().getGroupKey()).collection(constants.KEY_GROUP_USERS_COLLECTION).document(deleteUserUID));
        batch.update(fStore.collection(constants.KEY_GROUP_COLLECTION).document(User.getUser().getGroupKey()), constants.KEY_GROUP_USER_COUNT, FieldValue.increment(-1));
        batch.update(fStore.collection(constants.KEY_USER_COLLECTION).document(deleteUserUID), constants.KEY_USER_GROUP_KEY, "");

        batch.commit().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        response.setError(e.getMessage());
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            response.setData("ok");
                        }else{
                            response.setError(task.getException().getMessage());
                        }
                    }
                });
        return response;
    }
}
