package com.example.newapp.domain.models.chatModels;

import com.google.firebase.firestore.DocumentChange;

public class userStatusWithChangeType {
    public com.example.newapp.domain.models.chatModels.userStatus userStatus;
    public DocumentChange.Type changeType;

    public userStatusWithChangeType(com.example.newapp.domain.models.chatModels.userStatus userStatus, DocumentChange.Type changeType) {
        this.userStatus = userStatus;
        this.changeType = changeType;
    }
}
