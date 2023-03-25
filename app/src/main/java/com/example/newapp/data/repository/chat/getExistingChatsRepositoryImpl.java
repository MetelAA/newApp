package com.example.newapp.data.repository.chat;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.newapp.domain.models.chatModels.chatInfoWithSnapshotStatus;
import com.example.newapp.domain.models.chatModels.groupChatInfo;
import com.example.newapp.domain.models.chatModels.message;
import com.example.newapp.domain.models.chatModels.personChatInfo;
import com.example.newapp.domain.models.repository.chatRepository.getExistingChatsRepository;
import com.example.newapp.global.Response;
import com.example.newapp.global.User;
import com.example.newapp.global.constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class getExistingChatsRepositoryImpl implements getExistingChatsRepository {
    FirebaseFirestore fStore;
    ListenerRegistration listener;

   private int onCompleteCounter; //счётчик значение которого должно дойти до setDataResponseCounter
   private int  setDataResponseCounter;  //то число которого надо достичь чтобы сделать setData

    public getExistingChatsRepositoryImpl(FirebaseFirestore fStore) {
        this.fStore = fStore;
    }
    @Override
    public Response<ArrayList<chatInfoWithSnapshotStatus>, String> getExistingChats() {
        Response<ArrayList<chatInfoWithSnapshotStatus>, String> response = new Response<>();
        listener = fStore.collection(constants.KEY_CHAT_COLLECTION)
                .whereArrayContains(constants.KEY_CHAT_MEMBERS_UIDs, User.getUID())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {

                        if(e != null){
                            response.setError(e.getMessage());
                            return;
                        }
                        if(snapshots == null){
                            return;
                        }
                        setDataResponseCounter = snapshots.getDocumentChanges().size() * 2;
                        onCompleteCounter = 0;
                        ArrayList<chatInfoWithSnapshotStatus> chatInfosWithStatList = new ArrayList<>();
                        int i = 0;
                        for (DocumentChange change : snapshots.getDocumentChanges()) {
                            DocumentSnapshot document = change.getDocument();
                            if(TextUtils.equals(document.get(constants.KEY_CHAT_TYPE).toString(), constants.KEY_CHAT_TYPE_EQUALS_GROUP_CHAT)){
                                chatInfosWithStatList.add(new chatInfoWithSnapshotStatus(
                                        new groupChatInfo(
                                                constants.KEY_CHAT_TYPE_EQUALS_GROUP_CHAT,
                                                document.get(constants.KEY_CHAT_CHAT_ID).toString(),
                                                document.get(constants.KEY_CHAT_GROUP_CHAT_TITLE).toString()
                                        ),
                                        change.getType()
                                ));
                            }else{
                                List<String> chatMembersNames = (List<String>) document.get(constants.KEY_CHAT_MEMBERS_NAMEs);
                                String comradName = "";
                                List<String> chatMembersUIDs = (List<String>) document.get(constants.KEY_CHAT_MEMBERS_UIDs);
                                String comradUID = "";
                                if(TextUtils.equals(User.getUID(), chatMembersUIDs.get(0))) comradUID = chatMembersUIDs.get(1); else comradUID = chatMembersUIDs.get(0);
                                if(TextUtils.equals(User.getName(), chatMembersNames.get(0))) comradName = chatMembersNames.get(1); else comradName = chatMembersNames.get(0);
                                chatInfosWithStatList.add(new chatInfoWithSnapshotStatus(
                                        new personChatInfo(
                                                constants.KEY_CHAT_TYPE_EQUALS_PERSON_CHAT,
                                                document.get(constants.KEY_CHAT_CHAT_ID).toString(),
                                                comradName,
                                                comradUID
                                        ),
                                        change.getType()
                                ));
                            }

                            if(change.getType() == DocumentChange.Type.REMOVED){
                                response.setData(chatInfosWithStatList);
                                return;
                            }


                            if(document.get(constants.KEY_CHAT_LAST_MESSAGE_REFERENCE) == null){
                                return;
                            }
                            int finalI = i;
                            ((DocumentReference) document.get(constants.KEY_CHAT_LAST_MESSAGE_REFERENCE)).get()
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            response.setError(e.getMessage());
                                        }
                                    })
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            chatInfosWithStatList.get(finalI).chatInfo.setMessage(new message(
                                                            documentSnapshot.get(constants.KEY_CHAT_MESSAGE_SENDER_NAME).toString(),
                                                            documentSnapshot.get(constants.KEY_CHAT_MESSAGE_SENDER_UID).toString(),
                                                            documentSnapshot.get(constants.KEY_CHAT_MESSAGE).toString(),
                                                            documentSnapshot.getDate(constants.KEY_CHAT_MSG_SENT_TIME)
                                                    )
                                            );
                                            onCompleteCounter++;
                                            if(setDataResponseCounter == onCompleteCounter) response.setData(chatInfosWithStatList);
                                        }
                                    });
                            fStore.collection(constants.KEY_CHAT_COLLECTION)
                                    .document(chatInfosWithStatList.get(finalI).chatInfo.chatID)
                                    .collection(constants.KEY_CHAT_CHAT_MESSAGES_COLLECTION)
                                    .orderBy(constants.KEY_CHAT_MSG_SENT_TIME, Query.Direction.DESCENDING)
                                    .limit(999)
                                    .count()
                                    .get(AggregateSource.SERVER)
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            response.setError(e.getMessage());
                                        }
                                    })
                                    .addOnSuccessListener(new OnSuccessListener<AggregateQuerySnapshot>() {
                                        @Override
                                        public void onSuccess(AggregateQuerySnapshot aggregateQuerySnapshot) {
                                            long msgCount = aggregateQuerySnapshot.getCount();
                                            fStore.collection(constants.KEY_CHAT_COLLECTION)
                                                    .document(chatInfosWithStatList.get(finalI).chatInfo.chatID)
                                                    .collection(constants.KEY_CHAT_CHAT_MESSAGES_COLLECTION)
                                                    .orderBy(constants.KEY_CHAT_MSG_SENT_TIME, Query.Direction.DESCENDING)
                                                    .limit(999)
                                                    .whereArrayContainsAny(constants.KEY_CHAT_MESSAGE_READ_USERS_UIDs, Arrays.asList(User.getUID()))
                                                    .count()
                                                    .get(AggregateSource.SERVER)
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            response.setError(e.getMessage());
                                                        }
                                                    })
                                                    .addOnSuccessListener(new OnSuccessListener<AggregateQuerySnapshot>() {
                                                        @Override
                                                        public void onSuccess(AggregateQuerySnapshot aggregateQuerySnapshot) {
                                                            chatInfosWithStatList.get(finalI).chatInfo.setUnreadMessageCount(msgCount - aggregateQuerySnapshot.getCount());
                                                            onCompleteCounter++;
                                                            if(setDataResponseCounter == onCompleteCounter) response.setData(chatInfosWithStatList);
                                                        }
                                                    });
                                        }
                                    });
                            i++;
                        }
                    }
                });
        return response;
    }

}












