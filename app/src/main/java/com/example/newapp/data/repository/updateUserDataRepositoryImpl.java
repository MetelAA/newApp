package com.example.newapp.data.repository;



import androidx.annotation.NonNull;


import com.example.newapp.domain.models.repository.updateUserDataRepository;

import com.example.newapp.global.Response;
import com.example.newapp.global.User;
import com.example.newapp.global.constants;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User.getUser().create(
                                    UID,
                                    documentSnapshot.get(constants.KEY_USER_NAME).toString(),
                                    documentSnapshot.get(constants.KEY_USER_EMAIL).toString(),
                                    documentSnapshot.get(constants.KEY_USER_TYPE).toString()
                            );
                            String groupKey = documentSnapshot.get(constants.KEY_USER_GROUP_KEY).toString();
                            User.getUser().setGroupKey(groupKey);
                            if(!(documentSnapshot.get(constants.KEY_PROFILE_IMAGE) == null)){
                                User.getUser().setUserProfilePhotoUrl(documentSnapshot.get(constants.KEY_PROFILE_IMAGE).toString());
                            }else{
                                User.getUser().setUserProfilePhotoUrl(null);
                            }
                            response.setData("ok");
                        }
                    });
        }else {
            response.setData("User not sign in");
        }
        return response;
    }
}













