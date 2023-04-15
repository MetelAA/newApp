package com.example.newapp.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.example.newapp.MainActivity;
import com.example.newapp.databinding.ActivityLoginBinding;
import com.example.newapp.domain.models.loginUserData;
import com.example.newapp.global.User;
import com.example.newapp.global.constants;
import com.example.newapp.ui.profile.profileViewModelImpl;
import com.example.newapp.ui.registration.Registration;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    private ConstraintLayout mainElem;

    private EditText editTextEmail;
    private EditText editTextPassword;

    private ActivityLoginBinding binding;

    private loginViewModelImpl viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(loginViewModelImpl.class);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);

        mainElem = binding.getRoot();
        editTextEmail = binding.editTextEmailLog;
        editTextPassword = binding.editTextPasswordLog;


        setListeners();
        setObservers();
    }

    private void setObservers(){
        viewModel.onErrorLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Snackbar.make(mainElem, s, Snackbar.ANIMATION_MODE_SLIDE).show();
            }
        });
        viewModel.onUserLogin.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                viewModel.checkUser();
            }
        });

        viewModel.onGotUserData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }

    private void setListeners(){
        binding.loginButtonLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email =  editTextEmail.getText().toString().trim();
                String Password = editTextPassword.getText().toString().trim();
                if(TextUtils.isEmpty(Email)){
                    editTextEmail.setError("Введите Email");
                    return;
                }

                if(Password.length() < 8){
                    editTextPassword.setError("Пароль длинее 8 символов");
                    return;
                }

                loginUser(Email, Password);
            }
        });

        binding.redirectionToRegistrationLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(getApplicationContext(), Registration.class));
//                finish();
            }
        });
    }

    private void loginUser(String Email, String Password){
        viewModel.loginUser(new loginUserData(Email, Password));
    }

}