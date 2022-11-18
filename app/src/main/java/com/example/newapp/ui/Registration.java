package com.example.newapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.newapp.MainActivity;
import com.example.newapp.R;
import com.example.newapp.core.UpdateUserData;
import com.example.newapp.core.User;
import com.example.newapp.databinding.ActivityRegistrationBinding;
import com.example.newapp.interfaces.CallbackInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Registration extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    private ViewGroup mainElem;

    private EditText editTextPersonName;
    private EditText editTextEmailAddress;
    private EditText editTextPassword;
    private RadioGroup radioGroup;


    private ActivityRegistrationBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        mainElem = binding.getRoot();
        editTextPersonName = binding.editTextPersonNameReg;
        editTextEmailAddress = binding.editTextEmailAddressReg;
        editTextPassword = binding.editTextPasswordReg;
        radioGroup = binding.radioGroupReg;


        binding.createAccountBtnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton selectedBtn = findViewById(radioGroup.getCheckedRadioButtonId());
                String textPersonName, password, textEmailAddress, Type;
                textPersonName = editTextPersonName.getText().toString();
                password = editTextPassword.getText().toString();
                textEmailAddress = editTextEmailAddress.getText().toString();
                Type = selectedBtn.getText().toString();

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
        checkUser();
    }


    private void createUserAcc(String Name, String Password, String Email, String Type) {
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
                        if (task.isSuccessful()) {
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
                                            if (task.isSuccessful()) {
                                                User.getUser().create(UID, Name, Email, Type);
                                                User.getUser().setGroupKey("");
                                                User.getUser().setGroupName("");
                                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                finish();
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
    }


    private void createAdminAccAndGroup(String Name, String password, String Email, String Type, Context context) {

        final String[] groupName = new String[1];
        String groupKey = UUID.randomUUID().toString().substring(0, 8);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View customView = getLayoutInflater().inflate(R.layout.custom_alert_dialog_register, null);

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
                                        WriteBatch batch = fStore.batch();

                                        batch.set(fStore.collection("users").document(UID), userData);

                                        Map<String, String> groupData = new HashMap<>();
                                        groupData.put("nameGroup", groupName[0]);
                                        groupData.put("groupKey", groupKey);
                                        groupData.put("Admin", UID);
                                        batch.set(fStore.collection("groups").document(groupKey), groupData);

                                        Map<String, String> userGroupData = new HashMap<>();
                                        userGroupData.put("UID", UID);
                                        userGroupData.put("Name", Name);
                                        userGroupData.put("Email", Email);
                                        batch.set(fStore.collection("groups").document(groupKey).collection("groupUsers").document(UID), userGroupData);

                                        batch.update(fStore.collection("groups").document(groupKey), "numberUsers", FieldValue.increment(1));


                                        batch.commit()
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        fAuth.getCurrentUser().delete();
                                                        Snackbar.make(mainElem, e.getMessage(), Snackbar.LENGTH_LONG).show();
                                                    }
                                                })
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            User.getUser().create(UID, Name, Email, Type);
                                                            User.getUser().setGroupKey(groupKey);
                                                            User.getUser().setGroupName(groupName[0]);
                                                            Snackbar.make(mainElem, "Аккаунт успешно создан", Snackbar.LENGTH_LONG).show();
                                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                            finish();
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


    private void checkUser() { //хз мб както изменить тк эта хуйня повторяется уже дважды но хз
        UpdateUserData upData = new UpdateUserData(new CallbackInterface() {
            @Override
            public void callback(String status) {
                switch (status) {
                    case "ok":
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                        break;
                    case "error":

                        break;
                }
            }
        });
        upData.updateUserData(mainElem, fAuth, fStore);
    }
}
