package com.example.newapp.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newapp.R;
import com.example.newapp.core.GroupUserForListView;
import com.example.newapp.core.User;
import com.example.newapp.core.db.getDeleteUsers;
import com.example.newapp.interfaces.CallbackInterfaceWithList;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class AdapterListUsers extends RecyclerView.Adapter<AdapterListUsers.customViewHolder> {

    private ArrayList<GroupUserForListView> listUsers;

    public AdapterListUsers(ArrayList<GroupUserForListView> listUsers) {
        this.listUsers = listUsers;
    }



    @NonNull
    @Override
    public customViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_with_two_vertical_fields_and_btn, parent, false);
        return new customViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull customViewHolder holder, int position) {
        holder.bind(listUsers.get(position));
    }

    @Override
    public int getItemCount() {
        return listUsers.size();
    }

    class customViewHolder extends RecyclerView.ViewHolder{

        TextView nameTextInCustomListViewUsers;
        TextView emailTextInCustomListViewUsers;
        TextView hideUIDInCustomListViewUsers;

        public customViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextInCustomListViewUsers = itemView.findViewById(R.id.nameTextInCustomListViewWithTwoTextFieldsAndBtn);
            emailTextInCustomListViewUsers = itemView.findViewById(R.id.emailTextInCustomListViewWithTwoTextFieldsAndBtn);
            hideUIDInCustomListViewUsers = itemView.findViewById(R.id.hideUIDInCustomListViewWithTwoTextFieldsAndBtn);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                    View view = LayoutInflater.from(itemView.getContext()).inflate(R.layout.alert_dialog_exit, null);

                    TextView textView = view.findViewById(R.id.textCustomLogOutAlertDialog);
                    Button submit = view.findViewById(R.id.buttonCustomLogOutAlertDialogConfirm);
                    Button cancel = view.findViewById(R.id.buttonCustomLogOutAlertDialogCancel);

                    String kickUID = hideUIDInCustomListViewUsers.getText().toString();

                    if(TextUtils.equals(User.getUID(), kickUID)){
                        textView.setText("Вы не можете выгнать себя");
                        submit.setText("Выгнать");
                        submit.setVisibility(View.GONE);
                    }else{
                        textView.setText("Вы уверены что хотите выгнать " + nameTextInCustomListViewUsers.getText() + "?");
                        submit.setText("Выгнать");
                    }
                    cancel.setText("Отмена");
                    builder.setView(view);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        getDeleteUsers getDeleteUsers = new getDeleteUsers(new CallbackInterfaceWithList() {
                            @Override
                            public void requestResult(ArrayList list) {
                                listUsers.remove(getAdapterPosition());
                                notifyDataSetChanged();
                                alertDialog.cancel();
                            }

                            @Override
                            public void throwError(String error) {
                                Snackbar.make(itemView, error, Snackbar.LENGTH_LONG).show();
                            }
                        });
                        getDeleteUsers.kickUser(FirebaseFirestore.getInstance(), kickUID);
                        }
                    });

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.cancel();
                        }
                    });
                }
            });
        }

        public void bind(GroupUserForListView user){
            nameTextInCustomListViewUsers.setText(user.name);
            emailTextInCustomListViewUsers.setText(user.email);
            hideUIDInCustomListViewUsers.setText(user.UID);
            hideUIDInCustomListViewUsers.setVisibility(View.GONE);
        }
    }
}
