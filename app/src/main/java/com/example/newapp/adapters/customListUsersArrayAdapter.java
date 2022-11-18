package com.example.newapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.newapp.R;
import com.example.newapp.core.GroupUserForListView;

import java.util.ArrayList;

public class customListUsersArrayAdapter extends ArrayAdapter<GroupUserForListView> {

    public customListUsersArrayAdapter(Context context, ArrayList<GroupUserForListView> dataAdapter){
        super(context, R.layout.custom_list_item_for_list_of_users, dataAdapter);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        GroupUserForListView userForListView = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_item_for_list_of_users, parent, false);
        }

        TextView name = convertView.findViewById(R.id.nameTextInCustomListViewUsers);
        TextView email = convertView.findViewById(R.id.emailTextInCustomListViewUsers);
        TextView UID = convertView.findViewById(R.id.hideUIDInCustomListViewUsers);

        name.setText(userForListView.name);
        email.setText(userForListView.email);
        UID.setText(userForListView.UID);
        UID.setVisibility(View.GONE);

        return convertView;

    }

}