package com.example.newapp.core;

public class User {

    private static User user;
    private static String UID;
    private static String name;
    private static String email;
    private static String type;
    private String groupKey;
    private String groupName;
    private long numberUsers;


    public static synchronized User getUser() {
        if (user == null) {
            user = new User();
        }
        return user;
    }

    private User() {

    }

    public void create(String uid, String username, String Email, String Type) {
        UID = uid;
        name = username;
        email = Email;
        type = Type;
    }

    public static String getUID() {
        return UID;
    }

    public static String getName() {
        return name;
    }

    public static String getEmail() {
        return email;
    }

    public static String getType() {
        return type;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String group) {
        this.groupKey = group;
    }

    public String getGroupName() {
        return groupName;
    }

    public long getNumberUsers() {
        return numberUsers;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setNumberUsers(long numberUsers) {
        this.numberUsers = numberUsers;
    }

}
