package com.example.newapp.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.newapp.R;

import com.example.newapp.core.ExitJoinFromGroup;
import com.example.newapp.core.ListOfUsers;
import com.example.newapp.core.UpdateUserData;
import com.example.newapp.core.User;
import com.example.newapp.databinding.FragmentProfileBinding;
import com.example.newapp.interfaces.CallbackInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class profileFragment extends Fragment {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    private FragmentProfileBinding binding;

    private ImageButton profileImg;
    private Button userOptionGroupBtn;
    private Button groupSettingsBtn;

    private ViewGroup mainElem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View v = binding.getRoot();

        profileImg = binding.profileImage;

        userOptionGroupBtn = binding.btnProfileOptionGroup;

        groupSettingsBtn = binding.groupSettingsProfileBtn;

        mainElem = getActivity().findViewById(android.R.id.content);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        binding.logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

        showProfileData();
    }

    private void logOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        View customView = getLayoutInflater().inflate(R.layout.custom_alert_dialog_exit, null);
        builder.setView(customView);

        AlertDialog dialog = builder.create();
        dialog.show();

        Button dialogCancel = customView.findViewById(R.id.buttonCustomLogOutAlertDialogCancel);
        Button dialogConfirm = customView.findViewById(R.id.buttonCustomLogOutAlertDialogConfirm);

        dialogCancel.setText("Отмена");
        dialogConfirm.setText("Выйти");

        ((TextView) customView.findViewById(R.id.textCustomLogOutAlertDialog)).setText("Вы уверены, что хотите выйти из аккаунта?");


        dialogConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                startActivity(new Intent(getActivity(), Login.class));
                getActivity().finish();
            }
        });
        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }


    private void joinGroupAlertDialogListener() {
        userOptionGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View customView = getLayoutInflater().inflate(R.layout.custom_alert_dialog_join_group, null);
                builder.setView(customView);

                AlertDialog dialog = builder.create();
                dialog.show();

                Button dialogCancel = customView.findViewById(R.id.btnCustomJoinGroupAlertDialogCancel);
                Button dialogSubmit = customView.findViewById(R.id.btnCustomJoinGroupAlertDialogSubmit);

                EditText editText = customView.findViewById(R.id.editCustomJoinGroupAlertDialogField);

                dialogSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String editTextGroupKey = editText.getText().toString();
                        if (!TextUtils.isEmpty(editTextGroupKey)) {

                            ExitJoinFromGroup exitJoinFromGroup = new ExitJoinFromGroup(new CallbackInterface() {
                                @Override
                                public void callback(String status) {
                                    switch (status){
                                        case "ok":
                                            dialog.cancel();
                                            updateProfileData();
                                        break;
                                        case "no such group":
                                            editText.setError("Такой группы не существует");
                                        break;
                                        case "error":
                                            dialog.cancel();
                                        break;
                                    }
                                }
                            });
                            exitJoinFromGroup.joinGroup(mainElem, fStore, editTextGroupKey);
                        } else {
                            editText.setError("Введите код группы");
                        }
                    }
                });

                dialogCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

            }
        });
    }

    private void exitGroupAlertDialogListener() {
        userOptionGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                View customView = getLayoutInflater().inflate(R.layout.custom_alert_dialog_exit, null);
                builder.setView(customView);

                AlertDialog dialog = builder.create();
                dialog.show();

                Button dialogCancel = customView.findViewById(R.id.buttonCustomLogOutAlertDialogCancel);
                Button dialogConfirm = customView.findViewById(R.id.buttonCustomLogOutAlertDialogConfirm);

                dialogCancel.setText("Отмена");
                dialogConfirm.setText("Выйти");

                ((TextView) customView.findViewById(R.id.textCustomLogOutAlertDialog)).setText("Вы уверены, что хотите покинуть группу?");

                dialogConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ExitJoinFromGroup exitJoinFromGroup = new ExitJoinFromGroup(new CallbackInterface() {
                            @Override
                            public void callback(String status) {
                                switch (status){
                                    case "ok":
                                        dialog.cancel();
                                        updateProfileData();
                                        break;
                                    case "error":
                                        dialog.cancel();
                                        break;
                                }
                            }
                        });
                        exitJoinFromGroup.exitGroup(mainElem, fStore);
                    }
                });

                dialogCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
            }
        });
    }


    private void updateProfileData() {
        UpdateUserData upData = new UpdateUserData(new CallbackInterface() {
            @Override
            public void callback(String status) {
                switch (status){
                    case "ok":
                        showProfileData();
                    break;
                    case "error":

                    break;
                }
            }
        });
        upData.updateUserData(mainElem, fAuth, fStore);
    }

    private void showProfileData() { //Здесь 3 текстовых поля заполняются (не имеет смысла выносить в отдельные переменные тк используются только здесь)
        binding.profileNameText.setText(User.getName());
        binding.profileEmailText.setText(User.getEmail());
        binding.profileNameGroupText.setText(User.getUser().getGroupName());
        Log.d("Aboba",String.valueOf(User.getUser().getNumberUsers()));



        if (TextUtils.equals(User.getType(), "Учитель")) {
            groupSettingsBtn.setVisibility(View.VISIBLE);
            userOptionGroupBtn.setVisibility(View.INVISIBLE);
            groupSettingsBtnListener();
        } else {
            groupSettingsBtn.setVisibility(View.INVISIBLE);
        }

        if (!TextUtils.isEmpty(User.getUser().getGroupKey())) {
            Drawable drawableExit = getContext().getDrawable(R.drawable.btn_style_red);
            userOptionGroupBtn.setText("Выйти из группы");
            userOptionGroupBtn.setBackground(drawableExit);
            exitGroupAlertDialogListener();

            long numberGroup = User.getUser().getNumberUsers();
            if (numberGroup == 1) {
                binding.profileNumberGroupText.setText(numberGroup + " участник");
            } else if (User.getUser().getNumberUsers() <= 4) {
                binding.profileNumberGroupText.setText(numberGroup + " участника");
            } else if (User.getUser().getNumberUsers() > 4) {
                binding.profileNumberGroupText.setText(numberGroup + " участников");
            }
        } else {
            binding.profileNumberGroupText.setText("");
            Drawable drawableJoin = getContext().getDrawable(R.drawable.btn_style_blue_primary);
            userOptionGroupBtn.setText("Присоединиться к группе");
            userOptionGroupBtn.setBackground(drawableJoin);
            joinGroupAlertDialogListener();
        }
    }

    private void groupSettingsBtnListener() {
        groupSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListOfUsers listOfUsers = new ListOfUsers(getContext(), getLayoutInflater(), mainElem, fStore);
                listOfUsers.showListOfUsers();
            }
        });
    }

}