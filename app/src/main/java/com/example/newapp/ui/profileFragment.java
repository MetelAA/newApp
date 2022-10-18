package com.example.newapp.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
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

import com.example.newapp.Login;
import com.example.newapp.R;
import com.example.newapp.core.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class profileFragment extends Fragment {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;


    private ImageButton profileImg;
    private TextView profileName;
    private TextView profileEmail;
    private TextView nameGroup;
    private Button optionGroupBtn;
    private ImageButton logOutBtn;
    private ConstraintLayout mainElem;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        View v = inflater.inflate(R.layout.fragment_profile, container, false);


        mainElem = v.findViewById(R.id.mainElemProfile);

        profileImg = v.findViewById(R.id.profileFragment);
        profileName = v.findViewById(R.id.nameText);
        profileEmail = v.findViewById(R.id.emailText);
        nameGroup = v.findViewById(R.id.nameGroupText);

        profileImg = v.findViewById(R.id.profileImage);
        optionGroupBtn = v.findViewById(R.id.btnProfileOptionGroup);
        logOutBtn = v.findViewById(R.id.logOutBtn);

        checkUserGroup();


        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Aboba", " huita");
            }
        });

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                View customView = getLayoutInflater().inflate(R.layout.custom_alert_dilog_exit, null);
                builder.setView(customView);

                AlertDialog dialog = builder.create();
                dialog.show();

                Button dialogCancel = customView.findViewById(R.id.buttonCustomLogOutAlertDialogCancel);
                Button dialogSubmit = customView.findViewById(R.id.buttonCustomLogOutAlertDialogSubmit);
                ((TextView) customView.findViewById(R.id.textCustomLogOutAlertDialog)).setText("Вы уверены, что хотите выйти из аккаунта?");


                dialogSubmit.setOnClickListener(new View.OnClickListener() {
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
        });


        showProfileData();

        return v;
    }

    private void checkUserGroup() {
        if (!TextUtils.isEmpty(User.getUser().getGroupKey())) {
            Drawable drawableExit = getContext().getDrawable(R.drawable.btn_style_red);
            optionGroupBtn.setText("Выйти из группы");
            optionGroupBtn.setBackground(drawableExit);
            exitGroupAlertDialogListener();
        } else {
            Drawable drawableJoin = getContext().getDrawable(R.drawable.btn_style_blue_primary);
            optionGroupBtn.setText("Присоединиться к группе");
            optionGroupBtn.setBackground(drawableJoin);
            joinGroupAlertDialogListener();
        }
    }

    private void joinGroupAlertDialogListener() {
        optionGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View customView = getLayoutInflater().inflate(R.layout.custom_alert_dilog_join_group, null);
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
                            fStore.collection("groups").whereEqualTo("nameKey", editTextGroupKey).get()
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Snackbar.make(mainElem, e.getMessage(), Snackbar.LENGTH_LONG).show();
                                        }
                                    })
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                QuerySnapshot data = task.getResult();
                                                if (!data.isEmpty()) {
                                                    fStore.collection("users").document(User.getUID()).update("GroupKey", editTextGroupKey)
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
                                                                        Map<String, String> userGroupData = new HashMap<>();
                                                                        userGroupData.put("Type", "Ученик"); //тк админ только 1 и он задаётся при создании (мб переделать бд, то есть убрать присвоение типа пользователям в группе тк одмин всё равно только 1)
                                                                        userGroupData.put("UID", User.getUID());

                                                                        fStore.collection("groups").document(editTextGroupKey).collection("groupUsers").document(User.getUID()).set(userGroupData)
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

                                                                                            dialog.cancel();
                                                                                            showProfileData();
                                                                                        } else {
                                                                                            dialog.cancel();
                                                                                            Snackbar.make(mainElem, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                                                                            fStore.collection("users").document(User.getUID()).update("GroupKey", "")
                                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                                        @Override
                                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                                            Snackbar.make(mainElem, e.getMessage(), Snackbar.LENGTH_LONG).show();
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    }
                                                                                });

                                                                    } else {
                                                                        dialog.cancel();
                                                                        Snackbar.make(mainElem, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                                                    }
                                                                }
                                                            });
                                                } else {
                                                    editText.setError("такой группы не существует");
                                                }
                                            } else {
                                                Snackbar.make(mainElem, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                            }
                                        }
                                    });


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
        optionGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                View customView = getLayoutInflater().inflate(R.layout.custom_alert_dilog_exit, null);
                builder.setView(customView);

                AlertDialog dialog = builder.create();
                dialog.show();

                Button dialogCancel = customView.findViewById(R.id.buttonCustomLogOutAlertDialogCancel);
                Button dialogSubmit = customView.findViewById(R.id.buttonCustomLogOutAlertDialogSubmit);

                ((TextView) customView.findViewById(R.id.textCustomLogOutAlertDialog)).setText("Вы уверены, что хотите покинуть группу?");

                dialogSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

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


    private void showProfileData() {
        profileName.setText(User.getName());
        profileEmail.setText(User.getEmail());
        nameGroup.setText(User.getUser().getGroupName());
    }

}