package com.example.newapp.ui;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.newapp.R;

import com.example.newapp.adapters.AdapterListUsers;
import com.example.newapp.core.db.ExitJoinFromGroup;
import com.example.newapp.core.db.UpdateUserData;
import com.example.newapp.core.User;
import com.example.newapp.core.db.getDeleteUsers;
import com.example.newapp.databinding.FragmentProfileBinding;
import com.example.newapp.interfaces.CallbackInterface;
import com.example.newapp.interfaces.CallbackInterfaceWithList;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class profileFragment extends Fragment {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    private FragmentProfileBinding binding;

    private ImageButton profileImg;
    private Button userOptionGroupBtn;
    private Button groupUserListBtn;

    private ViewGroup mainElem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View v = binding.getRoot();

        profileImg = binding.profileImage;

        userOptionGroupBtn = binding.btnProfileOptionGroup;

        groupUserListBtn = binding.groupUserListProfileBtn;

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

        View customView = getLayoutInflater().inflate(R.layout.alert_dialog_exit, null);
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
                View customView = getLayoutInflater().inflate(R.layout.alert_dialog_join_group, null);
                builder.setView(customView);

                AlertDialog alertDialog = builder.create();


                Button dialogCancel = customView.findViewById(R.id.btnCustomJoinGroupAlertDialogCancel);
                Button dialogSubmit = customView.findViewById(R.id.btnCustomJoinGroupAlertDialogSubmit);

                EditText editText = customView.findViewById(R.id.editCustomJoinGroupAlertDialogField);

                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        View customViewForAnim = alertDialog.getWindow().getDecorView();
                        int height = customViewForAnim.getHeight();
                        int width = customViewForAnim.getWidth();

                        Animator animation =  ViewAnimationUtils.createCircularReveal(
                                customViewForAnim,
                                width / 2,
                                height / 2,
                                1F,
                                Math.max(width, height)
                        );

                        animation.setDuration(500);
                        animation.setInterpolator(new AccelerateDecelerateInterpolator());
                        animation.start();
                    }
                });

                alertDialog.show();

                dialogSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String editTextGroupKey = editText.getText().toString();
                        if (!TextUtils.isEmpty(editTextGroupKey)) {

                            ExitJoinFromGroup exitJoinFromGroup = new ExitJoinFromGroup(new CallbackInterface() {
                                @Override
                                public void requestStatus(String status) {
                                    switch(status){
                                        case "ok":
                                            alertDialog.cancel();
                                            updateProfileData();
                                        break;
                                        case "No such group":
                                            editText.setError("Группы с таким названием не сущесвтует");
                                        break;
                                    }
                                }

                                @Override
                                public void throwError(String error) {
                                    Snackbar.make(mainElem, error, Snackbar.LENGTH_LONG).show();
                                }
                            });
                            exitJoinFromGroup.joinGroup(fStore, editTextGroupKey);
                        } else {
                            editText.setError("Введите код группы");
                        }
                    }
                });

                dialogCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
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

                View customView = getLayoutInflater().inflate(R.layout.alert_dialog_exit, null);
                builder.setView(customView);

                AlertDialog alertDialog = builder.create();


                Button dialogCancel = customView.findViewById(R.id.buttonCustomLogOutAlertDialogCancel);
                Button dialogConfirm = customView.findViewById(R.id.buttonCustomLogOutAlertDialogConfirm);

                dialogCancel.setText("Отмена");
                dialogConfirm.setText("Выйти");

                ((TextView) customView.findViewById(R.id.textCustomLogOutAlertDialog)).setText("Вы уверены, что хотите покинуть группу?");

                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        View customViewForAnim = alertDialog.getWindow().getDecorView();
                        int height = customViewForAnim.getHeight();
                        int width = customViewForAnim.getWidth();

                        Animator animation =  ViewAnimationUtils.createCircularReveal(
                                customViewForAnim,
                                width / 2,
                                height / 2,
                                1F,
                                Math.max(width, height)
                        );

                        animation.setDuration(500);
                        animation.setInterpolator(new AccelerateDecelerateInterpolator());
                        animation.start();
                    }
                });

                alertDialog.show();

                dialogConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ExitJoinFromGroup exitJoinFromGroup = new ExitJoinFromGroup(new CallbackInterface() {
                            @Override
                            public void requestStatus(String result) {
                                //здесь он может вернуть только ок
                                alertDialog.cancel();
                                updateProfileData();
                            }

                            @Override
                            public void throwError(String error) {
                                alertDialog.cancel();
                                Snackbar.make(mainElem, error, Snackbar.LENGTH_LONG).show();
                            }
                        });
                        exitJoinFromGroup.exitGroup(fStore);
                    }
                });

                dialogCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });
            }
        });
    }


    private void updateProfileData() {
        UpdateUserData upData = new UpdateUserData(new CallbackInterface() {
            @Override
            public void requestStatus(String status) {
                showProfileData();
            }

            @Override
            public void throwError(String error) {
                Snackbar.make(mainElem, error, Snackbar.LENGTH_LONG).show();
            }
        });
        upData.updateUserData(fAuth, fStore);
    }

    private void showProfileData() { //Здесь 3 текстовых поля заполняются (не имеет смысла выносить в отдельные переменные тк используются только здесь)
        binding.profileNameText.setText(User.getName());
        binding.profileEmailText.setText(User.getEmail());
        binding.profileNameGroupText.setText(User.getUser().getGroupName());

        if (TextUtils.equals(User.getType(), "Учитель")) {
            groupUserListBtn.setVisibility(View.VISIBLE);
            userOptionGroupBtn.setVisibility(View.INVISIBLE);
            groupUserSettingsBtnListener();
        } else {
            groupUserListBtn.setVisibility(View.INVISIBLE);
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

    private void groupUserSettingsBtnListener() {
        groupUserListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                View customListViewInALDialog = getLayoutInflater().inflate(R.layout.recycler_view_screen, null);

                builder.setView(customListViewInALDialog);

                AlertDialog alertDialog = builder.create();



                getDeleteUsers getDeleteUsers = new getDeleteUsers(new CallbackInterfaceWithList() {
                    @Override
                    public void requestResult(ArrayList list) {
                        RecyclerView recyclerView = customListViewInALDialog.findViewById(R.id.RecyclerViewInCustomScreen);
                        showListOfUsers(list, recyclerView);
                    }
                    @Override
                    public void throwError(String error) {
                        Snackbar.make(mainElem, error, Snackbar.LENGTH_LONG).show();
                    }
                });

                getDeleteUsers.getListOfUsers(fStore);

                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        View customViewForAnim = alertDialog.getWindow().getDecorView();
                        int height = customViewForAnim.getHeight();
                        int width = customViewForAnim.getWidth();

                        Animator animation =  ViewAnimationUtils.createCircularReveal(
                                customViewForAnim,
                                width / 2,
                                height / 2,
                                1F,
                                Math.max(width, height)
                        );

                        animation.setDuration(600);
                        animation.setInterpolator(new AccelerateDecelerateInterpolator());
                        animation.start();
                    }
                });

                alertDialog.show();
            }
        });
    }

    private void showListOfUsers(ArrayList listUsers, RecyclerView recyclerView){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        AdapterListUsers adapter = new AdapterListUsers(listUsers);
        recyclerView.setAdapter(adapter);
    }

}