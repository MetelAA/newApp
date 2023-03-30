package com.example.newapp.data.repository;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.newapp.domain.models.repository.setUserProfileImageRepository;
import com.example.newapp.global.Response;
import com.example.newapp.global.User;
import com.example.newapp.global.constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class setUserProfileImageRepositoryImpl implements setUserProfileImageRepository {
    private StorageReference storageRef;
    private FirebaseFirestore fStore;

    public setUserProfileImageRepositoryImpl(StorageReference storageRef, FirebaseFirestore fStore) {
        this.storageRef = storageRef;
        this.fStore = fStore;
    }

    public Response<String, String> setUserProfileImage(Uri imageUri){
        Response<String, String> response = new Response<>();
        StorageReference path = storageRef.child(constants.KEY_STORAGE_COLLECTION_PROFILE_IMAGEs).child(User.getUID());
        path.putFile(imageUri)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        response.setError(e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        path.getDownloadUrl()
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        response.setError(e.getMessage());
                                    }
                                })
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        fStore.collection(constants.KEY_USER_COLLECTION).document(User.getUID())
                                                        .update(constants.KEY_PROFILE_IMAGE, uri.toString())
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    response.setError(e.getMessage());
                                                                }
                                                            })
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    response.setData("ok");
                                                                }
                                                            });

                                    }
                                });
                    }
                });
        return response;
    }
}
