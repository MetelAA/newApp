package com.example.newapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newapp.R;
import com.example.newapp.core.GroupUser;
import com.example.newapp.interfaces.RecyclerViewOnClickCallback;

import java.util.ArrayList;

public class AdapterShowListNewChats extends RecyclerView.Adapter<AdapterShowListNewChats.viewHolder> {

    ArrayList<GroupUser> arrayList;
    RecyclerViewOnClickCallback callback;

    public AdapterShowListNewChats(ArrayList<GroupUser> arrayList, RecyclerViewOnClickCallback callback) {
        this.arrayList = arrayList;
        this.callback = callback;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_for_show_list_chats_rec_view, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.bind(arrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class viewHolder extends RecyclerView.ViewHolder{
        TextView comradName;
        TextView hideUID;
        LinearLayout mainElem;
        TextView lastMessageTime;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            comradName = itemView.findViewById(R.id.comradNameShowListChats);
            hideUID = itemView.findViewById(R.id.lastMessageShowListChats);
            mainElem = itemView.findViewById(R.id.mainElemShowListChats);
            lastMessageTime = itemView.findViewById(R.id.lastMessageTimeShowListChats);

            mainElem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        public void bind(GroupUser user){
            comradName.setText(user.name);
            hideUID.setText(user.UID);
            hideUID.setVisibility(View.INVISIBLE);
            lastMessageTime.setVisibility(View.INVISIBLE);
        }
    }
}
