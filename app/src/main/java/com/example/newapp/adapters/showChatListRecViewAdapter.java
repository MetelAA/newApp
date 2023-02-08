package com.example.newapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newapp.R;
import com.example.newapp.domain.models.oldModels.chatInfoForShowChats;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class showChatListRecViewAdapter extends RecyclerView.Adapter<showChatListRecViewAdapter.viewHolder> {

    ArrayList<chatInfoForShowChats> list = new ArrayList<>();

    public showChatListRecViewAdapter(ArrayList<chatInfoForShowChats> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_for_show_list_chats_rec_view, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class viewHolder extends RecyclerView.ViewHolder{

        TextView ChatName;
        TextView lastMessage;
        TextView lastMessageTime;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            ChatName = itemView.findViewById(R.id.comradNameShowListChats);
            lastMessage = itemView.findViewById(R.id.lastMessageShowListChats);
            lastMessageTime = itemView.findViewById(R.id.lastMessageTimeShowListChats);
        }

        public void bind(chatInfoForShowChats chatInfo){
            ChatName.setText(chatInfo.chatName);
            lastMessage.setText(chatInfo.lastMessage);
            lastMessageTime.setText(new SimpleDateFormat("HH:mm").format(chatInfo.date));
        }

    }
}
