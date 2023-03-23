package com.example.newapp.domain.models.repository;

import com.example.newapp.global.Response;

public interface joinExitGroupRepository {
    Response<String, String> joinGroup(String groupKey);
    Response<String, String> exitGroup();
}
