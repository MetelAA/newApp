package com.example.newapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newapp.R;
import com.example.newapp.domain.models.groupUserData;
import com.example.newapp.interfaces.adapterOnClickInterface;


import java.util.ArrayList;


public class AdapterListUsers extends RecyclerView.Adapter<AdapterListUsers.customViewHolder> {

    private ArrayList<groupUserData> listUsers;
    private adapterOnClickInterface callback;

    public AdapterListUsers(ArrayList<groupUserData> listUsers, adapterOnClickInterface callback) {
        this.listUsers = listUsers;
        this.callback = callback;
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

    public void deleteGroupUserFromList(groupUserData deleteUser){
        listUsers.remove(deleteUser);
        notifyDataSetChanged();
    }
    class customViewHolder extends RecyclerView.ViewHolder{

        TextView nameText;
        TextView emailText;
        TextView userTypeText;

        public customViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.nameTextInCustomListViewWithTwoTextFieldsAndBtn);
            emailText = itemView.findViewById(R.id.emailTextInCustomListViewWithTwoTextFieldsAndBtn);
            userTypeText = itemView.findViewById(R.id.userTypeTextInCustomListViewWithTwoTextFieldsAndBtn);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.callback(listUsers.get(getAdapterPosition()));
                }
            });
        }

        public void bind(groupUserData user){
            nameText.setText(user.userName);
            emailText.setText(user.userEmail);
            userTypeText.setText(user.userType);
        }
    }
}
