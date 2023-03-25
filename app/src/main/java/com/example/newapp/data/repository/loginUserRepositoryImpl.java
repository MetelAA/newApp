package com.example.newapp.data.repository;

import androidx.annotation.NonNull;

import com.example.newapp.domain.models.loginUserData;
import com.example.newapp.domain.models.repository.loginUserRepository;
import com.example.newapp.global.Response;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class loginUserRepositoryImpl implements loginUserRepository {
    FirebaseAuth fAuth;

    public loginUserRepositoryImpl(FirebaseAuth fAuth) {
        this.fAuth = fAuth;
    }

    @Override
    public Response<String, String> loginUser(loginUserData data) {
        Response<String, String> response = new Response<>();

        fAuth.signInWithEmailAndPassword(data.email, data.password)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        response.setError(e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        response.setData("ok");
                    }
                });
       return response;
    }
}
