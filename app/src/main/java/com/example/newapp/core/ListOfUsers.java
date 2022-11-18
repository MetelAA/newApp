package com.example.newapp.core;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;

import com.example.newapp.adapters.customListUsersArrayAdapter;


import androidx.appcompat.app.AlertDialog;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.newapp.R;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;

import java.util.List;


public class ListOfUsers {

    private Context context;
    private LayoutInflater inflater;
    ViewGroup mainElem;
    FirebaseFirestore fStore;

    public ListOfUsers(Context context, LayoutInflater inflater, ViewGroup mainElem, FirebaseFirestore fStore) {
        this.context = context;
        this.inflater = inflater;
        this.mainElem = mainElem;
        this.fStore = fStore;
    }

    private customListUsersArrayAdapter customArrayAdapter;
    private ListView listView;


    public void showListOfUsers(){
        fStore.collection("groups").document(User.getUser().getGroupKey()).collection("groupUsers").get()
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
                            QuerySnapshot snapshot = task.getResult();
                            List<DocumentSnapshot> listData = snapshot.getDocuments();

                            ArrayList<GroupUserForListView> listUsers = new ArrayList<>();
                            for (DocumentSnapshot data : listData) {
                                if (!TextUtils.equals(data.get("UID").toString(), User.getUID())) {
                                    listUsers.add(new GroupUserForListView(data.get("Name").toString(), data.get("Email").toString(), data.get("UID").toString()));
                                }
                            }

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            View customView = inflater.inflate(R.layout.custom_alert_dialog_list_view, null);

                            listView = customView.findViewById(R.id.listViewInAlertDialog);

                            customArrayAdapter = new customListUsersArrayAdapter(context, listUsers);

                            listView.setAdapter(customArrayAdapter);
                            listView.setClickable(true);

                            builder.setView(customView);

                            AlertDialog listDialog = builder.create();
                            listDialog.show();

                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    kickUser(
                                            ((TextView) view.findViewById(R.id.nameTextInCustomListViewUsers)).getText().toString(),
                                            ((TextView) view.findViewById(R.id.hideUIDInCustomListViewUsers)).getText().toString(),
                                            position
                                    );
                                }
                            });
                        } else {
                            Snackbar.make(mainElem, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void kickUser(String Name, String kickUID, int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View customView = inflater.inflate(R.layout.custom_alert_dialog_exit, null);

        Button buttonConfirm = customView.findViewById(R.id.buttonCustomLogOutAlertDialogConfirm);
        Button buttonCancel = customView.findViewById(R.id.buttonCustomLogOutAlertDialogCancel);

        buttonConfirm.setText("Выгнать");
        buttonCancel.setText("Отмена");

        ((TextView) customView.findViewById(R.id.textCustomLogOutAlertDialog)).setText("Вы уверены что хотите выгнать " + Name);

        builder.setView(customView);
        AlertDialog dialog = builder.create();

        dialog.show();

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fStore.collection("groups").document(User.getUser().getGroupKey()).collection("groupUsers").document(kickUID).delete()
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
                                    fStore.collection("users").document(kickUID).update("GroupKey", "")
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
                                                        fStore.collection("groups").document(User.getUser().getGroupKey()).update("numberUsers", FieldValue.increment(-1))// надо типо ещё проверки но это слишком дохуя кода, и я не знаю нужно ли это делать
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if(task.isSuccessful()){
                                                                            customArrayAdapter.remove(customArrayAdapter.getItem(position));
                                                                            customArrayAdapter.notifyDataSetChanged();
                                                                            dialog.cancel();
                                                                        }else{
                                                                            dialog.cancel();
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
                                    Snackbar.make(mainElem, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

    }

}


