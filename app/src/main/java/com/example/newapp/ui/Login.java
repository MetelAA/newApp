package com.example.newapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.newapp.MainActivity;
import com.example.newapp.R;
import com.example.newapp.core.User;
import com.example.newapp.ui.Registration;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    private ConstraintLayout mainElem;

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button loginButtonLog;
    private Button redirectionToReg;

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
        fAuth.signInWithEmailAndPassword(Email, Password)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(mainElem, e.getMessage(), Snackbar.LENGTH_LONG);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            String UID = fAuth.getUid();
                            fStore.collection("users").document(UID).get()
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Snackbar.make(mainElem, e.getMessage(), Snackbar.LENGTH_LONG).show();
                                            fAuth.signOut();
                                        }
                                    })
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()){
                                                DocumentSnapshot userData = task.getResult();
                                                User.getUser().create(UID, userData.get("Name").toString(), userData.get("Email").toString(), userData.get("Type").toString());
                                                User.getUser().setGroupKey(userData.get("GroupKey").toString());
                                                if(!(TextUtils.isEmpty(userData.get("GroupKey").toString()))){
                                                    fStore.collection("groups").document(userData.get("GroupKey").toString()).get()
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    fAuth.signOut();
                                                                    Snackbar.make(mainElem, e.getMessage(), Snackbar.LENGTH_LONG).show();
                                                                }
                                                            })
                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    if(task.isSuccessful()){
                                                                        DocumentSnapshot groupData = task.getResult();
                                                                        User.getUser().setGroupName(groupData.get("nameGroup").toString());
                                                                    }else{
                                                                        fAuth.signOut();
                                                                        Snackbar.make(mainElem, task.getException().toString(), Snackbar.LENGTH_LONG).show();
                                                                    }
                                                                }
                                                            });
                                                }else{
                                                    User.getUser().setGroupName("");
                                                }
                                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                finish();
                                            }else{
                                                fAuth.signOut();
                                                Snackbar.make(mainElem, task.getException().toString(), Snackbar.LENGTH_LONG).show();
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