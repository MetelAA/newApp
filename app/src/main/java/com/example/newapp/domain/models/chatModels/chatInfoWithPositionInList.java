package com.example.newapp.domain.models.chatModels;

public class chatInfoWithPositionInList {
    public chatInfo chatInfo;
    public int position;

    public chatInfoWithPositionInList(com.example.newapp.domain.models.chatModels.chatInfo chatInfo, int position) {
        this.chatInfo = chatInfo;
        this.position = position;
    }
}
