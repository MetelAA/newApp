package com.example.newapp.domain.models.chatModels;

import java.io.Serializable;
import java.util.Date;

public class chatInfoForPossibleNewChat implements Serializable {
    public String comradUID, comradName, comradStatus, comradProfileMessageURL;
    private Date comradLastTimeSeen;
    private static final long serialVersionUID = 5L;


    public chatInfoForPossibleNewChat(String comradUID, String comradName, String comradStatus, String profileImage) {
        this.comradUID = comradUID;
        this.comradName = comradName;
        this.comradStatus = comradStatus;
        this.comradProfileMessageURL = profileImage;
    }

    public Date getComradLastTimeSeen() {
        return comradLastTimeSeen;
    }

    public void setComradLastTimeSeen(Date comradLastTimeSeen) {
        this.comradLastTimeSeen = comradLastTimeSeen;
    }

    @Override
    public String toString() {
        return "chatInfoForNewChat{" +
                "UID='" + comradUID + '\'' +
                ", Name='" + comradName + '\'' +
                ", Status='" + comradStatus + '\'' +
                ", profileImage='" + comradProfileMessageURL + '\'' +
                ", lastTimeSeen=" + comradLastTimeSeen +
                '}';
    }
}
