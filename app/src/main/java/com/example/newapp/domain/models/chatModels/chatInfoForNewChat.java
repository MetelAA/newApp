package com.example.newapp.domain.models.chatModels;

import java.util.Date;

public class chatInfoForNewChat {
    public String UID, Name, Status;
    private Date lastTimeSeen;

    public chatInfoForNewChat(String UID, String name, String status) {
        this.UID = UID;
        Name = name;
        Status = status;
    }

    public Date getLastTimeSeen() {
        return lastTimeSeen;
    }

    public void setLastTimeSeen(Date lastTimeSeen) {
        this.lastTimeSeen = lastTimeSeen;
    }

    @Override
    public String toString() {
        return "newChatInfo{" +
                "UID='" + UID + '\'' +
                ", Name='" + Name + '\'' +
                ", Status='" + Status + '\'' +
                ", date=" + lastTimeSeen +
                '}';
    }
}
