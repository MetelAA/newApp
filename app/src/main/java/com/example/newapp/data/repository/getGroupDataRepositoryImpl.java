package com.example.newapp.data.repository;

import androidx.annotation.NonNull;

import com.example.newapp.domain.models.profileGroupData;
import com.example.newapp.domain.models.repository.getUserGroupDataRepository;
import com.example.newapp.global.Response;
import com.example.newapp.global.User;
import com.example.newapp.global.constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class getGroupDataRepositoryImpl implements getUserGroupDataRepository {

    FirebaseFirestore fStore;

    public getGroupDataRepositoryImpl(FirebaseFirestore fStore) {
        this.fStore = fStore;
    }

    @Override
    public Response<profileGroupData, String> getProfileData() {
        Response<profileGroupData, String> response = new Response<>();

        fStore.collection(constants.KEY_GROUP_COLLECTION).document(User.getUser().getGroupKey()).get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        response.setError(e.getMessage());
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot snapshot = task.getResult();
                            response.setData(new profileGroupData(
                                    snapshot.get(constants.KEY_GROUP_NAME).toString(),
                                    (Long) snapshot.get(constants.KEY_GROUP_USER_COUNT)
                            ));
                        }else{
                            response.setError(task.getException().getMessage());
                        }
                    }
                });

        return response;
    }
}
