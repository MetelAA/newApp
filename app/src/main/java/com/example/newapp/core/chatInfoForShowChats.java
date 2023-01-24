package com.example.newapp.core;

import java.util.Date;

public class chatInfoForShowChats {
    public String chatName,
    lastMessage;
    public Date date;

    public chatInfoForShowChats(String chatName, String lastMessage, Date date) {
        this.chatName = chatName;
        this.lastMessage = lastMessage;
        this.date = date;
    }
}
