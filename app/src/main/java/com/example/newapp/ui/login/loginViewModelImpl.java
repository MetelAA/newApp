package com.example.newapp.ui.login;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.newapp.data.repository.loginUserRepositoryImpl;
import com.example.newapp.data.repository.updateUserDataRepositoryImpl;
import com.example.newapp.domain.models.loginUserData;
import com.example.newapp.domain.useCases.updateUserDataUseCase;
import com.example.newapp.interfaces.loginViewModel;
import com.example.newapp.domain.useCases.loginUserUseCase;
import com.example.newapp.global.Response;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Observable;
import java.util.Observer;

public class loginViewModelImpl extends ViewModel implements loginViewModel {
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    loginUserRepositoryImpl loginUserRepository = new loginUserRepositoryImpl(fAuth);
    loginUserUseCase loginUserUseCase = new loginUserUseCase(loginUserRepository);
    private final updateUserDataRepositoryImpl updateRepository = new updateUserDataRepositoryImpl(fStore, fAuth);
    private final updateUserDataUseCase updateUserDataUseCase = new updateUserDataUseCase(updateRepository);


    MutableLiveData<String> onErrorLiveData = new MutableLiveData<>();
    MutableLiveData<String> onUserLogin = new MutableLiveData<>();
    MutableLiveData<Boolean> onGotUserData = new MutableLiveData<>();

    public void checkUser() {
        Response<String, String> response = updateUserDataUseCase.checkUser();
        response.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                if(response.getError() == null){
                    switch(response.getData()){
                        case "ok":
                            onGotUserData.postValue(true);
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
    public void loginUser(loginUserData data) {
        Response<String, String> response = loginUserUseCase.loginUser(data);

        response.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                if(response.getError() == null){
                    onUserLogin.postValue("user login");
                }else{
                    onErrorLiveData.postValue(response.getError());
                }
                response.deleteObservers();
            }
        });
    }
}