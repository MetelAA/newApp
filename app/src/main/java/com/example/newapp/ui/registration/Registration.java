package com.example.newapp.ui.registration;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.newapp.MainActivity;
import com.example.newapp.R;
import com.example.newapp.databinding.ActivityRegistrationBinding;
import com.example.newapp.domain.models.registerAdminData;
import com.example.newapp.domain.models.registerUserData;
import com.example.newapp.global.User;
import com.example.newapp.ui.login.Login;
import com.google.android.material.snackbar.Snackbar;

public class Registration extends AppCompatActivity {
    private ConstraintLayout mainElem;
    private EditText editTextPersonName;
    private EditText editTextEmailAddress;
    private EditText editTextPassword;
    private RadioGroup radioGroup;
    private registrationViewModelImpl viewModel;


    private ActivityRegistrationBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        viewModel = new ViewModelProvider(this).get(registrationViewModelImpl.class);

        mainElem = binding.mainElemRegistrationAct;
        editTextPersonName = binding.editTextPersonNameReg;
        editTextEmailAddress = binding.editTextEmailAddressReg;
        editTextPassword = binding.editTextPasswordReg;
        radioGroup = binding.radioGroupReg;


        Log.d("Aboba", User.getName() + " user name");
        setListeners();
        addLiveDataObservers();
        checkUser();
    }


    private void addLiveDataObservers(){
        viewModel.onErrorLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Snackbar.make(mainElem, s, Snackbar.ANIMATION_MODE_SLIDE).show();
            }
        });
        viewModel.onGotUserData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean == true){
//                    Log.d("Aboba", "User{" +
//                            "userUID='" + User.getUID() + '\'' +
//                            "userEmail='" + User.getEmail() + '\'' +
//                            "userName='" + User.getName() + '\'' +
//                            "userType='" + User.getType() + '\'' +
//                            "userGroupKey='" + User.getUser().getGroupKey() + '\'' +
//                            "userImageURL='" + User.getUser().getUserProfilePhotoUrl() + '\'');

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            }
        });
    }


    private void createUserAcc(String name, String password, String email, String type) {
        Log.d("Aboba", "createUserAcc");
        viewModel.registerNewUser(new registerUserData(email, name, password, type));
    }


    private void createAdminAccAndGroup(String name, String password, String email, String type, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View customView = getLayoutInflater().inflate(R.layout.alert_dialog_register, null);

        EditText inputForNameGroupTextInAlertDialog = customView.findViewById(R.id.inputForNameGroupTextInAlertDialog);
        Button submitButton = customView.findViewById(R.id.submitButtonForAlertDialog);

        builder.setView(customView);
        AlertDialog dialog = builder.create();
        dialog.show();
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = inputForNameGroupTextInAlertDialog.getText().toString();
                if (!TextUtils.isEmpty(groupName)) {
                    dialog.dismiss();
                    viewModel.registerAdmin(new registerAdminData(email, name, password, type, groupName));
                } else {
                    inputForNameGroupTextInAlertDialog.setError("Введите название");
                }
            }
        });


    }

    private void setListeners(){
        binding.createAccountBtnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton selectedBtn = findViewById(radioGroup.getCheckedRadioButtonId());
                String textPersonName, password, textEmailAddress, Type;
                textPersonName = editTextPersonName.getText().toString().trim();
                password = editTextPassword.getText().toString().trim();
                textEmailAddress = editTextEmailAddress.getText().toString().trim();
                Type = selectedBtn.getText().toString().trim();

                if (TextUtils.isEmpty(textPersonName)) {
                    editTextPersonName.setError("Введите имя");
                    return;
                }

                if (TextUtils.isEmpty(textEmailAddress)) {
                    editTextEmailAddress.setError("Введите email");
                    return;
                }

                if (password.length() < 8) {
                    editTextPassword.setError("Пароль должен быть длинее 8 символов");
                    return;
                }

                if (Type.equals("Учитель")) {
                    createAdminAccAndGroup(textPersonName, password, textEmailAddress, Type, v.getContext());
                } else {
                    createUserAcc(textPersonName, password, textEmailAddress, Type);
                }
            }

        });
        binding.redirectionToLoginReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });
    }
    private void checkUser() {
        viewModel.checkUser();
    }
}
