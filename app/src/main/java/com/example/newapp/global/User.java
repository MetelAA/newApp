package com.example.newapp.global;

public class User {

    private static User user;
    private static String UID;
    private static String name;
    private static String email;
    private static String type;
    private String groupKey;
    private String userProfilePhotoUrl;



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

    public String getUserProfilePhotoUrl() {
        return userProfilePhotoUrl;
    }

    public void setUserProfilePhotoUrl(String userProfilePhotoUrl) {
        this.userProfilePhotoUrl = userProfilePhotoUrl;
    }
}
