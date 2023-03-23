package com.example.newapp.ui.registration;


import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.example.newapp.data.repository.registerNewUserOrAdminRepositoryImpl;
import com.example.newapp.data.repository.updateUserDataRepositoryImpl;
import com.example.newapp.domain.models.registerAdminData;
import com.example.newapp.domain.models.registerUserData;
import com.example.newapp.domain.useCases.registerUserUseCase;
import com.example.newapp.domain.useCases.updateUserDataUseCase;
import com.example.newapp.global.Response;
import com.example.newapp.global.User;
import com.example.newapp.interfaces.registrationViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Observable;
import java.util.Observer;

public class registrationViewModelImpl extends ViewModel implements registrationViewModel {

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    private final registerNewUserOrAdminRepositoryImpl registerRepository = new registerNewUserOrAdminRepositoryImpl(fStore, fAuth);
    private final updateUserDataRepositoryImpl updateRepository = new updateUserDataRepositoryImpl(fStore, fAuth);
    private final updateUserDataUseCase updateUserDataUseCase = new updateUserDataUseCase(updateRepository);
    private final registerUserUseCase registerUserUseCase = new registerUserUseCase(registerRepository);

    MutableLiveData<Boolean> onUserLogInLiveData = new MutableLiveData<>();
    MutableLiveData<String> onErrorLiveData = new MutableLiveData<>();

    @Override
    public void checkUser() {

        Response<String, String> response = updateUserDataUseCase.checkUser();
        response.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                if(response.getError() == null){
                    switch(response.getData()){
                        case "ok":
                            onUserLogInLiveData.postValue(true);
                        break;
                        case "User not sign in":

                        break;
                    }
                }else{
                    onErrorLiveData.postValue(response.getError());
                }
                response.deleteObservers();
            }
        });
    }

    @Override
    public void registerNewUser(registerUserData userData) {
        Response<String, String> response = registerUserUseCase.registerNewUser(userData);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                if(response.getError() == null){
                    onUserLogInLiveData.postValue(true);
                }else{
                    onErrorLiveData.postValue(response.getError());
                }
                response.deleteObservers();
            }
        });
    }

    @Override
    public void registerAdmin(registerAdminData adminData) {
        Response<String, String> response = registerUserUseCase.registerNewAdmin(adminData);
        response.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                if(response.getError() == null){
                    onUserLogInLiveData.postValue(true);
                }else{
                    onErrorLiveData.postValue(response.getError());
                }
                response.deleteObservers();
            }
        });
    }




}
