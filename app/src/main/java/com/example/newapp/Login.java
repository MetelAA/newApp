package com.example.newapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.example.newapp.core.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    ConstraintLayout mainElem;

    EditText editTextEmail;
    EditText editTextPassword;
    Button loginButtonLog;
    Button redirectionToReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        mainElem = findViewById(R.id.mainElemLogin);
        editTextEmail = findViewById(R.id.editTextEmailLog);
        editTextPassword = findViewById(R.id.editTextPasswordLog);
        loginButtonLog = findViewById(R.id.loginButtonLog);
        redirectionToReg = findViewById(R.id.redirectionToRegistrationLog);

        loginButtonLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email =  editTextEmail.getText().toString();
                String Password = editTextPassword.getText().toString();
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

        redirectionToReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Registration.class));
                finish();
            }
        });
    }

    private void loginUser(String Email, String Password){
        fAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String UID = fAuth.getUid();
                    fStore.collection("users").document(UID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot data = task.getResult();
                                User.getUser().create(UID, data.get("Name").toString(), data.get("Email").toString(), data.get("Type").toString());

                            }else{
                                Snackbar.make(mainElem, task.getException().toString(), Snackbar.LENGTH_LONG).show();
                                fAuth.signOut();
                            }
                        }
                    });
                }else{
                    Snackbar.make(mainElem, task.getException().toString(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }







}