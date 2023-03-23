package com.example.newapp.interfaces;

import android.net.Uri;

public interface profileViewModel {

    void initProfileData();

    void getListGroupUsers();

    void kickGroupUser(String deleteUserUID);

    void joinGroup(String groupKey);

    void exitGroup();

    void exitAcc();

    void setUserProfileImage(Uri imageUri);

}
