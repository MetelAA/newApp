package com.example.newapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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

import com.example.newapp.core.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Registration extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    ConstraintLayout mainElem;

    EditText editTextPersonName;
    EditText editTextEmailAddress;
    EditText editTextPassword;
    RadioGroup radioGroup;
    Button createAccountBtnReg;
    Button redirectionToLogin;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        checkUser();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mainElem = findViewById(R.id.mainElemRegistration);
        editTextPersonName = findViewById(R.id.editTextPersonNameReg);
        editTextEmailAddress = findViewById(R.id.editTextEmailAddressReg);
        editTextPassword = findViewById(R.id.editTextPasswordReg);
        radioGroup = findViewById(R.id.radioGroupReg);
        createAccountBtnReg = findViewById(R.id.createAccountBtnReg);
        redirectionToLogin = findViewById(R.id.redirectionToLoginReg);







        createAccountBtnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton selectedBtn =  findViewById(radioGroup.getCheckedRadioButtonId());
                String textPersonName, password, textEmailAddress, Type;
                textPersonName = editTextPersonName.getText().toString();
                password = editTextPassword.getText().toString();
                textEmailAddress = editTextEmailAddress.getText().toString();
                Type = selectedBtn.getText().toString();

                if(TextUtils.isEmpty(textPersonName)){
                    editTextPersonName.setError("Введите имя");
                    return;
            }

                if(TextUtils.isEmpty(textEmailAddress)){
                    editTextEmailAddress.setError("Введите email");
                    return;
                }

                if(password.length() <= 8){
                    editTextPassword.setError("Пароль должен быть длинее 8 символов");
                    return;
                }

                if(Type.equals("Учитель")){
                    createAdminAccAndGroup(textPersonName, password, textEmailAddress, Type, v.getContext());
                }else{
                    createUserAcc(textPersonName, password, textEmailAddress, Type);
                }
            }
        });



        redirectionToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });


    }



    private void createUserAcc(String Name, String Password, String Email, String Type){
        fAuth.createUserWithEmailAndPassword(Email, Password)

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
                    String UID = fAuth.getCurrentUser().getUid(); //не уверен что именно current (проверить)
                    Map<String, String> data = new HashMap<>();
                    data.put("Name", Name);
                    data.put("Email", Email);
                    data.put("Type", Type);
                    data.put("GroupKey", "");
                    fStore.collection("users").document(UID).set(data)

                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Snackbar.make(mainElem, e.getMessage(), Snackbar.LENGTH_LONG).show();
                                }
                            })

                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        User.getUser().create(UID, Name, Email, Type);
                                        User.getUser().setGroupKey("");
                                        User.getUser().setGroupName("");
                                        Snackbar.make(mainElem, "Аккаунт успешно создан", Snackbar.LENGTH_LONG).show();
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        finish();
                                    }else{
                                        fAuth.getCurrentUser().delete();
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



    private void createAdminAccAndGroup(String Name, String password, String Email, String Type, Context context) {

        final String[] groupName = new String[1];
        String groupKey = UUID.randomUUID().toString().substring(0, 8);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View customView = getLayoutInflater().inflate(R.layout.custom_alert_dilog_register, null);

        EditText inputForNameGroupTextInAlertDialog = customView.findViewById(R.id.inputForNameGroupTextInAlertDialog);
        Button submitButton = customView.findViewById(R.id.submitButtonForAlertDialog);

        builder.setView(customView);
        AlertDialog dialog = builder.create();
        dialog.show();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupName[0] = inputForNameGroupTextInAlertDialog.getText().toString();

                if (!TextUtils.isEmpty(groupName[0])) {
                    dialog.dismiss();

                    Map<String, String> userData = new HashMap<>();
                    userData.put("Name", Name);
                    userData.put("Email", Email);
                    userData.put("Type", Type);
                    userData.put("GroupKey", groupKey);
                    //сделать индкатор загрузки
                    fAuth.createUserWithEmailAndPassword(Email, password)

                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) { //проверить куда выкидывать будет
                                    Snackbar.make(mainElem, e.getMessage(), Snackbar.LENGTH_LONG).show();
                                }
                            })

                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        String UID = fAuth.getCurrentUser().getUid();
                                        fStore.collection("users").document(UID).set(userData)

                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) { //проверить куда выкидывать будет
                                                        fAuth.getCurrentUser().delete();
                                                        fStore.collection("users").document(UID).delete();
                                                        Snackbar.make(mainElem, e.getMessage(), Snackbar.LENGTH_LONG).show();
                                                    }
                                                })

                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Map<String, String> groupData = new HashMap<>();
                                                            groupData.put("nameGroup", groupName[0]);
                                                            groupData.put("nameKey", groupKey);
                                                            fStore.collection("groups").document(groupKey).set(groupData)

                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) { //проверить куда выкидывать будет
                                                                            fAuth.getCurrentUser().delete();
                                                                            fStore.collection("users").document(UID).delete();
                                                                            Snackbar.make(mainElem, e.getMessage(), Snackbar.LENGTH_LONG).show();
                                                                        }
                                                                    })

                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                Map<String, String> userGroupData = new HashMap<>();
                                                                                userGroupData.put("UID", UID);
                                                                                userGroupData.put("Type", Type);
                                                                                fStore.collection("groups").document(groupKey).collection("groupUsers").document(UID).set(userGroupData)

                                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                                            @Override
                                                                                            public void onFailure(@NonNull Exception e) { //проверить куда выкидывать будет
                                                                                                fAuth.getCurrentUser().delete();
                                                                                                fStore.collection("users").document(UID).delete();
                                                                                                fStore.collection("groups").document(groupKey).delete();
                                                                                                Snackbar.make(mainElem, e.getMessage(), Snackbar.LENGTH_LONG).show();
                                                                                            }
                                                                                        })

                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if (task.isSuccessful()) {
                                                                                                    User.getUser().create(UID, Name, Email, Type);
                                                                                                    User.getUser().setGroupKey(groupKey);
                                                                                                    User.getUser().setGroupName(groupName[0]);
                                                                                                    Snackbar.make(mainElem, "Аккаунт успешно создан", Snackbar.LENGTH_LONG).show();
                                                                                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                                                                    finish();
                                                                                                } else {
                                                                                                    fAuth.getCurrentUser().delete();
                                                                                                    fStore.collection("users").document(UID).delete();
                                                                                                    fStore.collection("groups").document(groupKey).delete();
                                                                                                    Snackbar.make(mainElem, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                                                                                }
                                                                                            }
                                                                                        });
                                                                            } else {
                                                                                fAuth.getCurrentUser().delete();
                                                                                fStore.collection("users").document(UID).delete();
                                                                                Snackbar.make(mainElem, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                                                            }
                                                                        }
                                                                    });
                                                        } else {
                                                            fAuth.getCurrentUser().delete();
                                                            Snackbar.make(mainElem, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        Snackbar.make(mainElem, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                    }
                                }
                            });
                } else {
                    inputForNameGroupTextInAlertDialog.setError("Введите название");
                }
            }
        });
    }




    private void checkUser(){
        if(fAuth.getUid() != null){
            String UID = fAuth.getUid();

            fStore.collection("users").document(UID).get()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //обрабтать ошибку
                            Snackbar.make(mainElem, e.getMessage(), Snackbar.LENGTH_LONG);
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot userData = task.getResult();
                        User.getUser().create(UID, userData.get("Name").toString(), userData.get("Email").toString(), userData.get("Type").toString());
                        User.getUser().setGroupKey(userData.get("GroupKey").toString());
                        if(!(TextUtils.equals(userData.get("GroupKey").toString(), ""))){
                            fStore.collection("groups").document(userData.get("GroupKey").toString()).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()){
                                                DocumentSnapshot groupData = task.getResult();
                                                User.getUser().setGroupName(groupData.get("nameGroup").toString());
                                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                finish();
                                            }else{
                                                //отрабатывать ошибку
                                                Snackbar.make(mainElem, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }else{
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            User.getUser().setGroupName("");
                            finish();
                        }
                    }else{
                        Snackbar.make(mainElem, task.getException().getMessage(), Snackbar.LENGTH_LONG);
                    }
                }
            });
        }else {
            return;
        }
    }







}
