package com.example.newapp.data.repository;


import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.newapp.domain.models.repository.updateUserDataRepository;
import com.example.newapp.global.User;
import com.example.newapp.global.constants;
import com.example.newapp.ui.registration.registrationViewModelImpl;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;



public class updateUserDataRepositoryImpl implements updateUserDataRepository {
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();

    registrationViewModelImpl viewModel;

    public updateUserDataRepositoryImpl(registrationViewModelImpl viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void checkUser() {
        if(fAuth.getUid() != null){
            String UID = fAuth.getUid();
            fStore.collection(constants.KEY_USER_COLLECTION).document(UID).get()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            viewModel.onError(e.getMessage());
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
                                viewModel.onUserLogin(true);
                            }else{
                                viewModel.onError(task.getException().getMessage());
                            }
                        }
                    });
        }else {
            viewModel.onUserLogin(false);
        }
    }
}

