package com.example.newapp.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.example.newapp.MainActivity;
import com.example.newapp.databinding.ActivityLoginBinding;
import com.example.newapp.global.User;
import com.example.newapp.global.constants;
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

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    private ConstraintLayout mainElem;

    private EditText editTextEmail;
    private EditText editTextPassword;

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();




        mainElem = binding.getRoot();
        editTextEmail = binding.editTextEmailLog;
        editTextPassword = binding.editTextPasswordLog;


        binding.loginButtonLog.setOnClickListener(new View.OnClickListener() {
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

        binding.redirectionToRegistrationLog.setOnClickListener(new View.OnClickListener() {
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
                            fStore.collection(constants.KEY_USER_COLLECTION).document(UID).get()
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
                                                User.getUser().create(UID,
                                                        userData.get(constants.KEY_USER_NAME).toString(),
                                                        userData.get(constants.KEY_USER_EMAIL).toString(),
                                                        userData.get(constants.KEY_USER_TYPE).toString()
                                                        );
                                                User.getUser().setGroupKey(userData.get(constants.KEY_USER_GROUP_KEY).toString());
                                                if(!(TextUtils.isEmpty(userData.get(constants.KEY_USER_GROUP_KEY).toString()))){
                                                    fStore.collection(constants.KEY_GROUP_COLLECTION).document(userData.get(constants.KEY_USER_GROUP_KEY).toString()).get()
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
                                                                        User.getUser().setGroupName(groupData.get(constants.KEY_GROUP_NAME).toString());
                                                                    }else{
                                                                        fAuth.signOut();
                                                                        Snackbar.make(mainElem, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
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
                                                Snackbar.make(mainElem, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }else{
                            Snackbar.make(mainElem, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

}