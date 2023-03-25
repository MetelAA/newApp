package com.example.newapp.domain.models;

public class profileDataAboutGroup {
    public String groupName;
    public long countGroupUsers;

    public profileDataAboutGroup(String groupName, long countGroupUsers) {
        this.groupName = groupName;
        this.countGroupUsers = countGroupUsers;
    }
}
