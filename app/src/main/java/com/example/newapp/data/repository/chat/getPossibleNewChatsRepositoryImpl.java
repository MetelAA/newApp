package com.example.newapp.data.repository.chat;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.example.newapp.domain.models.chatModels.chatInfoForPossibleNewChat;
import com.example.newapp.domain.models.repository.chatRepository.getPossibleNewChatsRepository;
import com.example.newapp.global.Response;
import com.example.newapp.global.User;
import com.example.newapp.global.constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class getPossibleNewChatsRepositoryImpl implements getPossibleNewChatsRepository {
    FirebaseFirestore fStore;

    public getPossibleNewChatsRepositoryImpl(FirebaseFirestore fStore) {
        this.fStore = fStore;
    }

    Response<ArrayList<chatInfoForPossibleNewChat>, String> response;

    @Override
    public Response<ArrayList<chatInfoForPossibleNewChat>, String> getPossibleNewChats(ArrayList<String> existingChatsMemberUID) {
        response = new Response<>();

        fStore.collection(constants.KEY_GROUP_COLLECTION)
                .document(User.getUser().getGroupKey())
                .collection(constants.KEY_GROUP_USERS_COLLECTION)
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
                        ;
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            usersUIDs.add(document.get(constants.KEY_USER_UID).toString());
                        }
                        for (int i = 0; i < existingChatsMemberUID.size(); i++) {
                            usersUIDs.remove(existingChatsMemberUID.get(i));
                        }
                       // Log.d("Aboba", String.valueOf(usersUIDs.size()));
                        int baseListSize = usersUIDs.size();
                        int iIter;
                        int remain;
                        if (baseListSize % 10 == 0) {
                            iIter = baseListSize / 10;
                        } else {
                            iIter = baseListSize / 10 + 1;
                        }
                        for (int i = 0; i < iIter; i++) {
                            remain = baseListSize % 10;
                            if (remain == 0) remain = 10;
                            int from = baseListSize - remain;
                            int to = baseListSize;
                            List<String> subList = usersUIDs.subList(from, to);
                            getPossibleNewChats(subList);
                            baseListSize -= remain;
                        }

                    }
                });
        return response;
    }

    private void getPossibleNewChats(List<String> pieceOfPossibleChats) {
        fStore.collection(constants.KEY_USER_COLLECTION)
                .whereIn(FieldPath.documentId(), pieceOfPossibleChats)
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
                        ArrayList<chatInfoForPossibleNewChat> listPossibleChats = new ArrayList<>();
                        int i = 0;
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            String comradProfileImage = null;
                            if (document.get(constants.KEY_PROFILE_IMAGE) != null) {
                                comradProfileImage = document.get(constants.KEY_PROFILE_IMAGE).toString();
                            }
                            listPossibleChats.add(
                                    new chatInfoForPossibleNewChat(
                                            document.getId(),
                                            document.get(constants.KEY_USER_NAME).toString(),
                                            document.get(constants.KEY_USER_STATUS).toString(),
                                            comradProfileImage
                                    )
                            );
                            if (TextUtils.equals(document.get(constants.KEY_USER_STATUS).toString(), constants.KEY_USER_STATUS_EQUALS_OFFLINE)) {
                                listPossibleChats.get(i).setComradLastTimeSeen(document.getDate(constants.KEY_USER_LAST_TIME_SEEN));
                            }
                            i++;
                        }
                        //Log.d("Aboba", listPossibleChats.toString());
                        response.setData(listPossibleChats);
                    }
                });
    }
}
