package com.example.newapp.ui.login;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.newapp.data.repository.loginUserRepositoryImpl;
import com.example.newapp.domain.models.loginUserData;
import com.example.newapp.interfaces.loginViewModel;
import com.example.newapp.domain.useCases.loginUserUseCase;
import com.example.newapp.global.Response;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Observable;
import java.util.Observer;

public class loginViewModelImpl extends ViewModel implements loginViewModel {
    FirebaseAuth fAuth = FirebaseAuth.getInstance();

    loginUserRepositoryImpl loginUserRepository = new loginUserRepositoryImpl(fAuth);
    loginUserUseCase loginUserUseCase = new loginUserUseCase(loginUserRepository);


    MutableLiveData<String> onErrorLiveData = new MutableLiveData<>();
    MutableLiveData<String> onUserLogin = new MutableLiveData<>();


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