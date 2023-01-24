package com.example.newapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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
import com.example.newapp.core.db.UpdateUserData;
import com.example.newapp.core.User;
import com.example.newapp.core.db.createAcc;
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

    private ConstraintLayout mainElem;

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


        mainElem = binding.mainElemRegistrationAct;
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
        createAcc createAcc = new createAcc(new CallbackInterface() {
            @Override
            public void requestStatus(String status) {
                Snackbar.make(mainElem, "аккаунт успешно создан", Snackbar.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }

            @Override
            public void throwError(String error) {
                Snackbar.make(mainElem, error, Snackbar.LENGTH_LONG).show();
            }
        });
        createAcc.createUserProfile(fAuth, fStore, Name, Password, Email, Type);
    }


    private void createAdminAccAndGroup(String Name, String password, String Email, String Type, Context context) {
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
                    createAcc createAcc = new createAcc(new CallbackInterface() {
                        @Override
                        public void requestStatus(String status) {
                            Snackbar.make(mainElem, "аккаунт успешно создан", Snackbar.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }

                        @Override
                        public void throwError(String error) {
                            Snackbar.make(mainElem, error, Snackbar.LENGTH_LONG).show();
                        }
                    });
                    createAcc.createAdminAcc(fAuth, fStore, Name, password, Email, Type, groupName);
                } else {
                    inputForNameGroupTextInAlertDialog.setError("Введите название");
                }
            }
        });
    }


    private void checkUser() { //хз мб както изменить тк эта хуйня повторяется уже дважды но хз
        UpdateUserData upData = new UpdateUserData(new CallbackInterface() {
            @Override
            public void requestStatus(String status) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }

            @Override
            public void throwError(String error) {
                if(TextUtils.equals("Not sigh up", error)){
                    return;
                }
                Snackbar.make(mainElem, error, Snackbar.LENGTH_LONG).show();
            }
        });
        upData.updateUserData(fAuth, fStore);
    }
}
