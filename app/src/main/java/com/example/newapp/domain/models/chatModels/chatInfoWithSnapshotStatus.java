package com.example.newapp.domain.models.chatModels;

import com.example.newapp.domain.models.chatModels.chatInfo;
import com.google.firebase.firestore.DocumentChange;

public class chatInfoWithSnapshotStatus {
    public chatInfo chatInfo;
    public DocumentChange.Type changeType;

    public chatInfoWithSnapshotStatus(com.example.newapp.domain.models.chatModels.chatInfo chatInfo, DocumentChange.Type changeType) {
        this.chatInfo = chatInfo;
        this.changeType = changeType;
    }
}
