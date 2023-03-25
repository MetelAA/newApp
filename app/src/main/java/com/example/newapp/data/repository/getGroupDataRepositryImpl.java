package com.example.newapp.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.newapp.domain.models.profileDataAboutGroup;
import com.example.newapp.domain.models.repository.getProfileDataRepositry;
import com.example.newapp.global.Response;
import com.example.newapp.global.User;
import com.example.newapp.global.constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class getGroupDataRepositryImpl implements getProfileDataRepositry {

    FirebaseFirestore fStore;

    public getGroupDataRepositryImpl(FirebaseFirestore fStore) {
        this.fStore = fStore;
    }

    @Override
    public Response<profileDataAboutGroup, String> getProfileData() {
        Response<profileDataAboutGroup, String> response = new Response<>();

        fStore.collection(constants.KEY_GROUP_COLLECTION).document(User.getUser().getGroupKey()).get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        response.setError(e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        response.setData(new profileDataAboutGroup(
                                documentSnapshot.get(constants.KEY_GROUP_NAME).toString(),
                                (Long) documentSnapshot.get(constants.KEY_GROUP_USER_COUNT)
                        ));
                    }
                });

        return response;
    }
}
