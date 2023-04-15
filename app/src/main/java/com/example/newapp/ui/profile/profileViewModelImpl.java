package com.example.newapp.ui.profile;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.newapp.data.repository.getDeleteGroupUsersRepositoryImpl;
import com.example.newapp.data.repository.getGroupDataRepositryImpl;
import com.example.newapp.data.repository.joinExitGroupRepositoryImpl;
import com.example.newapp.data.repository.setUserProfileImageRepositoryImpl;
import com.example.newapp.domain.models.groupUserData;
import com.example.newapp.domain.models.profileDataAboutGroup;
import com.example.newapp.domain.useCases.getUserGroupDataUseCase;
import com.example.newapp.domain.useCases.getListKickGroupUsersUseCase;
import com.example.newapp.domain.useCases.joinExitGroupUseCase;
import com.example.newapp.domain.useCases.setUserProfileImageUseCase;
import com.example.newapp.global.Response;
import com.example.newapp.global.User;
import com.example.newapp.interfaces.profileViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;


public class profileViewModelImpl extends ViewModel implements profileViewModel {

    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private final FirebaseStorage fStorage = FirebaseStorage.getInstance();

    private final getGroupDataRepositryImpl getProfileData = new getGroupDataRepositryImpl(fStore);
    private final getDeleteGroupUsersRepositoryImpl getDeleteGroupUsers = new getDeleteGroupUsersRepositoryImpl(fStore);
    private final joinExitGroupRepositoryImpl joinExitGroup = new joinExitGroupRepositoryImpl(fStore);
    private final setUserProfileImageRepositoryImpl setUserProfileImage = new setUserProfileImageRepositoryImpl(fStorage.getReference(), fStore);
    private final setUserProfileImageUseCase userProfileImageUseCase = new setUserProfileImageUseCase(setUserProfileImage);
    private final getUserGroupDataUseCase getUserGroupDataUseCase = new getUserGroupDataUseCase(getProfileData);
    private final getListKickGroupUsersUseCase getListKickGroupUsersUseCase = new getListKickGroupUsersUseCase(getDeleteGroupUsers);
    private final joinExitGroupUseCase joinExitGroupUseCase = new joinExitGroupUseCase(joinExitGroup);



    MutableLiveData<String> onErrorLiveData = new MutableLiveData<>();
    MutableLiveData<profileDataAboutGroup> onGotProfileGroupDataLiveData = new MutableLiveData<>(); //если она будет обновленна это служит гарантией что апдейт тоже произошёл
    MutableLiveData<ArrayList<groupUserData>> onGotListGroupUsesLiveData = new MutableLiveData<>();
    MutableLiveData<String> onUserDeletedLiveData = new MutableLiveData<>();

    MutableLiveData<String> userProfileImageSet = new MutableLiveData<>();
    MutableLiveData<String> userJoinGroup = new MutableLiveData<>();
    MutableLiveData<String> userExitGroup = new MutableLiveData<>();

    @Override
    public void initProfileGroupData() {
        Response<profileDataAboutGroup, String> response = getUserGroupDataUseCase.getProfileData();
        response.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if(response.getError() == null){
                    onGotProfileGroupDataLiveData.postValue(response.getData());
                }else{
                    onErrorLiveData.postValue(response.getError());
                }
                response.deleteObservers();
            }
        });
    }



    @Override
    public void setUserProfileImage(Uri imageUri) {
        Response<String, String> response = userProfileImageUseCase.setUserImageProfile(imageUri);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if(response.getError() == null){
                    Log.d("Aboba", "on update profileImage viewModel");
                    userProfileImageSet.postValue(response.getData());
                }else{
                    onErrorLiveData.postValue(response.getError());
                }
                response.deleteObservers();
            }
        });
    }


    @Override
    public void getListGroupUsers() {
        Response<ArrayList<groupUserData>, String> response = getListKickGroupUsersUseCase.getListGroupUsers();
        response.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                if(response.getError() == null){
                    onGotListGroupUsesLiveData.postValue(response.getData());
                }else{
                    onErrorLiveData.postValue(response.getError());
                }
                response.deleteObservers();
            }
        });
    }

    @Override
    public void kickGroupUser(String deleteUserUID) {
        Response<String, String> response =  getListKickGroupUsersUseCase.deleteUser(deleteUserUID);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                if(response.getError() == null){
                    onUserDeletedLiveData.postValue("user " + deleteUserUID + " delete");
                }else{
                    onErrorLiveData.postValue(response.getError());
                }
                response.deleteObservers();
            }
        });
    }

    @Override
    public void joinGroup(String groupKey) {
        Response<String, String> response = joinExitGroupUseCase.joinGroup(groupKey);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                if(response.getError() == null){
                    userJoinGroup.postValue(response.getData());;
                }else{
                    onErrorLiveData.postValue(response.getError());
                }
                response.deleteObservers();
            }
        });
    }

    @Override
    public void exitGroup() {
        Response<String, String> response = joinExitGroupUseCase.exitGroup();
        response.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                if(response.getError() == null){
                    userExitGroup.postValue(response.getData());
                }else{
                    onErrorLiveData.postValue(response.getError());
                }
                response.deleteObservers();
            }
        });
    }

    @Override
    public void exitAcc() {
        fAuth.signOut();
    }

}
