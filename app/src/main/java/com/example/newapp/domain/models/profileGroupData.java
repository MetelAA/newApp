package com.example.newapp.domain.models;

public class profileGroupData {
    public String groupName;
    public long countGroupUsers;

    public profileGroupData(String groupName, long countGroupUsers) {
        this.groupName = groupName;
        this.countGroupUsers = countGroupUsers;
    }
}
