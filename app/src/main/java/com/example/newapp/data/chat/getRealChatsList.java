package com.example.newapp.data.chat;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.example.newapp.global.User;
import com.example.newapp.domain.models.oldModels.chatInfoForShowChats;
import com.example.newapp.global.constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class getRealChatsList {


//
//    private ArrayList<chatInfoForShowChats> listChats = new ArrayList<>();
//
//
//
//    public void getChatList(FirebaseFirestore fStore){
//        fStore.collection(constants.KEY_CHAT_COLLECTION)
//                .whereArrayContains(constants.KEY_CHAT_MEMBERS_UIDs, User.getUID())
//                .orderBy(constants.KEY_CHAT_LAST_MESSAGE_TIME ,Query.Direction.DESCENDING)
//                .get()
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        callback.throwError(e.getMessage());
//                    }
//                })
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful()){
//                            List<DocumentSnapshot> documentList = task.getResult().getDocuments();
//                            for (DocumentSnapshot document:documentList){
//                                Date date = ((Timestamp) document.get(constants.KEY_CHAT_LAST_MESSAGE_TIME)).toDate();
//                                if(TextUtils.equals(document.get(constants.KEY_CHAT_TYPE).toString(), constants.KEY_CHAT_TYPE_EQUALS_GROUP_CHAT)){
//                                    listChats.add(new chatInfoForShowChats(
//                                            document.get(constants.KEY_CHAT_GROUP_CHAT_TITLE).toString(),
//                                            document.get(constants.KEY_CHAT_LAST_MESSAGE).toString(),
//                                            date
//                                    ));
//                                }else{
//                                    String[] dialogMembers = (String[]) document.get(constants.KEY_CHAT_MEMBERS_UIDs);
//                                    for (int i = 0; i < 2; i++) {
//                                        if(!TextUtils.equals(dialogMembers[i], User.getUID())){
//                                            listChats.add(new chatInfoForShowChats(
//                                                    dialogMembers[i],
//                                                    document.get(constants.KEY_CHAT_LAST_MESSAGE).toString(),
//                                                    date
//                                            ));
//                                        }
//                                    }
//                                }
//                            }
//                            callback.requestResult(listChats);
//                        }else{
//                            callback.throwError(task.getException().getMessage());
//                        }
//                    }
//                });
//    }
}
