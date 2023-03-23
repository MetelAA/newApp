package com.example.newapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newapp.R;
import com.example.newapp.domain.models.chatModels.chatInfo;
import com.example.newapp.domain.models.chatModels.groupChatInfo;
import com.example.newapp.domain.models.chatModels.personChatInfo;
import com.example.newapp.global.User;
import com.example.newapp.global.constants;
import com.example.newapp.interfaces.adapterOnClickInterface;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class showChatsListRecViewAdapter extends RecyclerView.Adapter<showChatsListRecViewAdapter.viewHolder> {

    private ArrayList<chatInfo> list;
    private adapterOnClickInterface callback;

    public showChatsListRecViewAdapter(ArrayList<chatInfo> list, adapterOnClickInterface callback) {
        this.list = list;
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
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class viewHolder extends RecyclerView.ViewHolder{

        TextView chatName;
        TextView lastMessage;
        TextView lastMessageTime;
        TextView messageStatus;

        TextView lastMessageSenderName;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            chatName = itemView.findViewById(R.id.comradNameShowListChats);
            lastMessage = itemView.findViewById(R.id.lastMessageShowListChats);
            lastMessageTime = itemView.findViewById(R.id.lastMessageTimeShowListChats);
            messageStatus = itemView.findViewById(R.id.messageStatusShowListChats);
            lastMessageSenderName = itemView.findViewById(R.id.lastMessageSenderNameShowListChats);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.callback(list.get(getAdapterPosition()));
                }
            });
        }

        public void bind(chatInfo chatInfo){
            if(TextUtils.equals(chatInfo.chatType, constants.KEY_CHAT_TYPE_EQUALS_GROUP_CHAT)){
                groupChatInfo groupChatInfo = (groupChatInfo) chatInfo;
                chatName.setText(groupChatInfo.chatTitle);
            }else{
                personChatInfo personChatInfo = (personChatInfo) chatInfo;
                chatName.setText(personChatInfo.comradName);
            }
            if(chatInfo.getUnreadMessageCount() == 0){
                messageStatus.setVisibility(View.INVISIBLE);
            }else{
                messageStatus.setVisibility(View.VISIBLE);
                messageStatus.setText(String.valueOf(chatInfo.getUnreadMessageCount()));
            }


            if(!TextUtils.equals(User.getUID(), chatInfo.getMessage().senderUID)) {
                lastMessageSenderName.setText(chatInfo.getMessage().senderName + ": ");
            }

            lastMessage.setText(chatInfo.getMessage().messageText);
            lastMessageTime.setText(new SimpleDateFormat("HH:mm").format(chatInfo.getMessage().messageSentTime));
        }
    }
}
