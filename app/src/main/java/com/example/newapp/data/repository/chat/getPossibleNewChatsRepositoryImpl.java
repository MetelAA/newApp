package com.example.newapp.data.repository.chat;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.example.newapp.domain.models.chatModels.chatInfoForNewChat;
import com.example.newapp.domain.models.repository.chatRepository.getPossibleNewChatsRepository;
import com.example.newapp.global.Response;
import com.example.newapp.global.User;
import com.example.newapp.global.constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class getPossibleNewChatsRepositoryImpl implements getPossibleNewChatsRepository {
    FirebaseFirestore fStore;

    public getPossibleNewChatsRepositoryImpl(FirebaseFirestore fStore) {
        this.fStore = fStore;
    }

    @Override
    public Response<ArrayList<chatInfoForNewChat>, String> getPossibleNewChats(ArrayList<String> existingChatsMemberUID) {
        Response<ArrayList<chatInfoForNewChat>, String> response = new Response<>();

        fStore.collection(constants.KEY_GROUP_COLLECTION)
                .document(User.getUser().getGroupKey())
                .collection(constants.KEY_GROUP_USERS_COLLECTION)
                .whereNotEqualTo(constants.KEY_USER_UID, existingChatsMemberUID)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        response.setError(e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<String> usersUIDs = new ArrayList<>();
                        ArrayList<String> usersNames = new ArrayList<>();
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {

                            //Log.d("Aboba", "first onComplete " + document.getData());
                            usersUIDs.add(document.get(constants.KEY_USER_UID).toString());
                            usersNames.add(document.get(constants.KEY_USER_NAME).toString());
                        }
                        fStore.collection(constants.KEY_USER_COLLECTION)
                                .whereIn(FieldPath.documentId(), usersUIDs)
                                .get()
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        response.setError(e.getMessage());
                                    }
                                })
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        ArrayList<chatInfoForNewChat> listPossibleChats = new ArrayList<>();
                                        int i=0;
                                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                            listPossibleChats.add(new chatInfoForNewChat(
                                                    usersUIDs.get(i),
                                                    usersNames.get(i),
                                                    document.get(constants.KEY_USER_STATUS).toString()
                                            ));
                                            if(TextUtils.equals(document.get(constants.KEY_USER_STATUS).toString(), constants.KEY_USER_STATUS_EQUALS_OFFLINE)){
                                                listPossibleChats.get(i).setLastTimeSeen(document.getDate(constants.KEY_USER_LAST_TIME_SEEN));
                                            }
                                            i++;
                                        }
                                        response.setData(listPossibleChats);
                                    }
                                });
                    }
                });
        return response;
    }
}
