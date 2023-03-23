package com.example.newapp.domain.models.comparators;

import com.example.newapp.domain.models.chatModels.chatInfo;

import java.util.Comparator;

public class chatInfoComparator implements Comparator<chatInfo> {
    @Override
    public int compare(chatInfo chatInfo1, chatInfo chatInfo2) {
        Long date1 = chatInfo1.getMessage().messageSentTime.getTime();
        Long date2 = chatInfo2.getMessage().messageSentTime.getTime();
        if(date1 > date2){
            return -1;
        }else if(date1 < date2){
            return 1;
        }else{
            return 0;
        }
    }
}
