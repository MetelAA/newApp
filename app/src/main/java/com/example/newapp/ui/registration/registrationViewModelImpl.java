package com.example.newapp.ui.registration;


import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.example.newapp.data.repository.registerNewUserOrAdminRepositoryImpl;
import com.example.newapp.data.repository.updateUserDataRepositoryImpl;
import com.example.newapp.domain.models.registerAdminData;
import com.example.newapp.domain.models.registerUserData;
import com.example.newapp.domain.useCases.registration.registerUserUseCase;
import com.example.newapp.domain.useCases.registration.updateUserDataUseCase;
import com.example.newapp.interfaces.registrationViewModel;

public class registrationViewModelImpl extends ViewModel implements registrationViewModel {

    private final registerNewUserOrAdminRepositoryImpl registerRepository = new registerNewUserOrAdminRepositoryImpl(this);
    private final updateUserDataRepositoryImpl updateRepository = new updateUserDataRepositoryImpl(this);
    private final updateUserDataUseCase updateUserDataUseCase = new updateUserDataUseCase(updateRepository);
    private final registerUserUseCase registerUserUseCase = new registerUserUseCase(registerRepository);

    MutableLiveData<String> onErrorLiveData = new MutableLiveData<>();
    MutableLiveData<Boolean> userStatus = new MutableLiveData<>();


    @Override
    public void checkUser() {
        updateUserDataUseCase.checkUser();
    }

    @Override
    public void registerNewUser(registerUserData userData) {
        registerUserUseCase.registerNewUser(userData);
    }

    @Override
    public void registerAdmin(registerAdminData adminData) {
        registerUserUseCase.registerNewAdmin(adminData);
    }

    @Override
    public void onError(String error) {
        onErrorLiveData.postValue(error);
    }

    @Override
    public void onUserLogin(Boolean status) {
        userStatus.postValue(status);
    }


}
