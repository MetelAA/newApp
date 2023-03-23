package com.example.newapp.ui.profile;

import android.net.Uri;
import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.newapp.data.repository.getDeleteGroupUsersRepositoryImpl;
import com.example.newapp.data.repository.getGroupDataRepositoryImpl;
import com.example.newapp.data.repository.joinExitGroupRepositoryImpl;
import com.example.newapp.data.repository.setUserProfileImageRepositoryImpl;
import com.example.newapp.data.repository.updateUserDataRepositoryImpl;
import com.example.newapp.domain.models.groupUserData;
import com.example.newapp.domain.models.profileGroupData;
import com.example.newapp.domain.useCases.getUserGroupDataUseCase;
import com.example.newapp.domain.useCases.getListKickGroupUsersUseCase;
import com.example.newapp.domain.useCases.joinExitGroupUseCase;
import com.example.newapp.domain.useCases.setUserProfileImageUseCase;
import com.example.newapp.domain.useCases.updateUserDataUseCase;
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

    private FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private FirebaseStorage fStorage = FirebaseStorage.getInstance();

    private updateUserDataRepositoryImpl updateUserData = new updateUserDataRepositoryImpl(fStore, fAuth);
    private getGroupDataRepositoryImpl getProfileData = new getGroupDataRepositoryImpl(fStore);
    private getDeleteGroupUsersRepositoryImpl getDeleteGroupUsers = new getDeleteGroupUsersRepositoryImpl(fStore);
    private joinExitGroupRepositoryImpl joinExitGroup = new joinExitGroupRepositoryImpl(fStore);
    private setUserProfileImageRepositoryImpl setUserProfileImage = new setUserProfileImageRepositoryImpl(fStorage.getReference());
    private setUserProfileImageUseCase userProfileImageUseCase = new setUserProfileImageUseCase(setUserProfileImage);
    private getUserGroupDataUseCase getUserGroupDataUseCase = new getUserGroupDataUseCase(getProfileData);
    private updateUserDataUseCase updateUserDataUseCase = new updateUserDataUseCase(updateUserData);
    private getListKickGroupUsersUseCase getListKickGroupUsersUseCase = new getListKickGroupUsersUseCase(getDeleteGroupUsers);
    private joinExitGroupUseCase joinExitGroupUseCase = new joinExitGroupUseCase(joinExitGroup);



    MutableLiveData<String> onErrorLiveData = new MutableLiveData<>();
    MutableLiveData<profileGroupData> onGotProfileDataLiveData = new MutableLiveData<>(); //если она будет обновленна это служит гарантией что апдейт тоже произошёл
    MutableLiveData<ArrayList<groupUserData>> onGotListGroupUsesLiveData = new MutableLiveData<>();
    MutableLiveData<String> onUserDeletedLiveData = new MutableLiveData<>();
    MutableLiveData<String> onCompleteNeedToUpdate = new MutableLiveData<>();

    @Override
    public void initProfileData() {
        Response<String, String> updateResponse = updateUserDataUseCase.checkUser();
        updateResponse.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                if(updateResponse.getError() == null){
                    if(!TextUtils.isEmpty(User.getUser().getGroupKey())) {
                        Response<profileGroupData, String> getDataResponse = getUserGroupDataUseCase.getProfileData();
                        getDataResponse.addObserver(new Observer() {
                            @Override
                            public void update(Observable observable, Object o) {
                                if (getDataResponse.getError() == null) {
                                    onGotProfileDataLiveData.postValue(getDataResponse.getData());
                                } else {
                                    onErrorLiveData.postValue(getDataResponse.getError());
                                }
                            }
                        });
                    }else{
                        onGotProfileDataLiveData.postValue(new profileGroupData("", 0));
                    }

                }else{
                    onErrorLiveData.postValue(updateResponse.getError());
                }
                updateResponse.deleteObservers();
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
                    onCompleteNeedToUpdate.postValue(response.getData());
                }else{
                    onErrorLiveData.postValue(response.getError());
                    response.deleteObservers();
                }
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
                    onCompleteNeedToUpdate.postValue("user join group");;
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
                    onCompleteNeedToUpdate.postValue("user exit group");
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