package com.example.newapp.core.db;


import androidx.annotation.NonNull;

import com.example.newapp.core.GroupUser;
import com.example.newapp.core.User;
import com.example.newapp.core.constants;
import com.example.newapp.interfaces.CallbackInterfaceWithList;
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

public class getDeleteGroupUsers {

    CallbackInterfaceWithList callback;

    public getDeleteGroupUsers(CallbackInterfaceWithList callback) {
        this.callback = callback;
    }

    public void getListOfUsers(FirebaseFirestore fStore){
        fStore.collection(constants.KEY_GROUP_COLLECTION).document(User.getUser().getGroupKey()).collection(constants.KEY_GROUP_USERS_COLLECTION).get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.throwError(e.getMessage());
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            ArrayList<GroupUser> listUsers = new ArrayList<>();
                            QuerySnapshot queryDocumentSnapshots = task.getResult();
                            List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot document:documents) {
                                listUsers.add(new GroupUser(
                                        document.get(constants.KEY_USER_NAME).toString(),
                                        document.get(constants.KEY_USER_EMAIL).toString(),
                                        document.get(constants.KEY_USER_UID).toString())
                                );
                            }
                            callback.requestResult(listUsers);
                        }else{
                            callback.throwError(task.getException().getMessage());
                        }
                    }
                });

    }

    public void kickUser(FirebaseFirestore fStore, String kickUID){
        WriteBatch batch = fStore.batch();
        batch.delete(fStore.collection(constants.KEY_GROUP_COLLECTION).document(User.getUser().getGroupKey()).collection(constants.KEY_GROUP_USERS_COLLECTION).document(kickUID));
        batch.update(fStore.collection(constants.KEY_GROUP_COLLECTION).document(User.getUser().getGroupKey()), constants.KEY_GROUP_USER_COUNT, FieldValue.increment(-1));
        batch.update(fStore.collection(constants.KEY_USER_COLLECTION).document(kickUID), constants.KEY_USER_GROUP_KEY, "");

                batch.commit().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.throwError(e.getMessage());
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            callback.requestResult(null);
                        }else{
                            callback.throwError(task.getException().getMessage());
                        }
                    }
                });
    }

}


