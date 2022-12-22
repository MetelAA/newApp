package com.example.newapp.core.db;


import androidx.annotation.NonNull;

import com.example.newapp.core.GroupUserForListView;
import com.example.newapp.core.User;
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

public class getDeleteUsers {

    CallbackInterfaceWithList callback;

    public getDeleteUsers(CallbackInterfaceWithList callback) {
        this.callback = callback;
    }

    public void getListOfUsers(FirebaseFirestore fStore){
        fStore.collection("groups").document(User.getUser().getGroupKey()).collection("groupUsers").get()
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
                            ArrayList<GroupUserForListView> listUsers = new ArrayList<>();
                            QuerySnapshot queryDocumentSnapshots = task.getResult();
                            List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot document:documents) {
                                listUsers.add(new GroupUserForListView(document.get("Name").toString(), document.get("Email").toString(), document.get("UID").toString()));
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
        batch.delete(fStore.collection("groups").document(User.getUser().getGroupKey()).collection("groupUsers").document(kickUID));
        batch.update(fStore.collection("groups").document(User.getUser().getGroupKey()), "numberUsers", FieldValue.increment(-1));
        batch.update(fStore.collection("users").document(kickUID), "GroupKey", "");

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


