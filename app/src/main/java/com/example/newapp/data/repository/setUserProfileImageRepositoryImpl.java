package com.example.newapp.data.repository;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.newapp.domain.models.repository.setUserProfileImageRepository;
import com.example.newapp.global.Response;
import com.example.newapp.global.User;
import com.example.newapp.global.constants;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.nio.file.Path;

public class setUserProfileImageRepositoryImpl implements setUserProfileImageRepository {
    StorageReference storageRef;

    public setUserProfileImageRepositoryImpl(StorageReference storageRef) {
        this.storageRef = storageRef;
    }

    public Response<String, String> setUserProfileImage(Uri imageUri){
        Response<String, String> response = new Response<>();
        StorageReference path = storageRef.child(constants.KEY_STORAGE_PROFILE_IMAGE).child(User.getUID());
        path.putFile(imageUri).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                response.setError(e.getMessage());
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    path.getDownloadUrl()
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                response.setError(e.getMessage());
                            }
                        })
                        .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                response.setData("ok");
                                Log.d("Aboba", task.getResult().toString());
                            }else{
                                response.setError(task.getException().getMessage());
                            }
                        }
                        });
                }else{
                    response.setError(task.getException().getMessage());
                }
        }
        });
        return response;
    }
}
