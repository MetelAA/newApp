package com.example.newapp.domain.models.repository;

import com.example.newapp.domain.models.groupUserData;
import com.example.newapp.global.Response;

import java.util.ArrayList;
import java.util.List;

public interface getDeleteGroupUsersRepository {
    Response<ArrayList<groupUserData>, String> getUsersList();
    Response<String, String> deleteUser(String deleteUserUID);
}
