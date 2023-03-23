package com.example.newapp.domain.useCases;

import com.example.newapp.domain.models.repository.joinExitGroupRepository;
import com.example.newapp.global.Response;

public class joinExitGroupUseCase {
    joinExitGroupRepository joinExitGroupRepository;

    public joinExitGroupUseCase(com.example.newapp.domain.models.repository.joinExitGroupRepository joinExitGroupRepository) {
        this.joinExitGroupRepository = joinExitGroupRepository;
    }

    public Response<String, String> joinGroup(String groupKey){
        return joinExitGroupRepository.joinGroup(groupKey);
    }

    public Response<String, String> exitGroup(){
        return joinExitGroupRepository.exitGroup();
    }
}
