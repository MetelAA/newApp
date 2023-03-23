package com.example.newapp.data.repository;



import androidx.annotation.NonNull;


import com.example.newapp.domain.models.repository.updateUserDataRepository;

import com.example.newapp.global.Response;
import com.example.newapp.global.User;
import com.example.newapp.global.constants;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;



public class updateUserDataRepositoryImpl implements updateUserDataRepository {
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;


    public updateUserDataRepositoryImpl(FirebaseFirestore fStore, FirebaseAuth fAuth) {
        this.fStore = fStore;
        this.fAuth = fAuth;
    }

    @Override
    public Response<String, String> checkUser() {
        Response<String, String> response = new Response<>();

        if(fAuth.getUid() != null){
            String UID = fAuth.getUid();
            fStore.collection(constants.KEY_USER_COLLECTION).document(UID).get()
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
                                DocumentSnapshot userData = task.getResult();
                                User.getUser().create(
                                        UID,
                                        userData.get(constants.KEY_USER_NAME).toString(),
                                        userData.get(constants.KEY_USER_EMAIL).toString(),
                                        userData.get(constants.KEY_USER_TYPE).toString()
                                );
                                String GroupKey = userData.get(constants.KEY_USER_GROUP_KEY).toString();
                                User.getUser().setGroupKey(GroupKey);
                                response.setData("ok");
                            }else{
                                response.setError(task.getException().getMessage());
                            }
                        }
                    });
        }else {
            response.setData("User not sign in");
        }
        return response;
    }
}













