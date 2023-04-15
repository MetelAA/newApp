package com.example.newapp.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newapp.R;
import com.example.newapp.domain.models.chatModels.chatInfo;
import com.example.newapp.domain.models.chatModels.groupChatInfo;
import com.example.newapp.domain.models.chatModels.personChatInfo;
import com.example.newapp.global.User;
import com.example.newapp.global.constants;
import com.example.newapp.interfaces.adapterOnClickInterface;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class showChatsListRecViewAdapter extends RecyclerView.Adapter<showChatsListRecViewAdapter.viewHolder> {

    private ArrayList<chatInfo> list;
    private adapterOnClickInterface callback;

    private Context context;

    public showChatsListRecViewAdapter(ArrayList<chatInfo> list, adapterOnClickInterface callback) {
        this.list = list;
        this.callback = callback;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_for_show_list_chats_rec_view, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Log.d("Aboba", "bind   " + list.toString());
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
        CircleImageView chatImageView;

        TextView lastMessageSenderName;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            chatImageView = itemView.findViewById(R.id.chatImageView);
            chatName = itemView.findViewById(R.id.comradNameShowListChats);
            lastMessage = itemView.findViewById(R.id.lastMessageShowListChats);
            lastMessageTime = itemView.findViewById(R.id.lastMessageTimeShowListChats);
            lastMessageSenderName = itemView.findViewById(R.id.lastMessageSenderNameShowListChats);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.callback(list.get(getAdapterPosition()));
                }
            });
        }

        public void bind(chatInfo chatInfo){
//            Log.d("Aboba", "bind  -- " + chatInfo.toString());
            if(TextUtils.equals(chatInfo.chatType, constants.KEY_CHAT_TYPE_EQUALS_GROUP_CHAT)){
                groupChatInfo groupChatInfo = (groupChatInfo) chatInfo;
                chatName.setText(groupChatInfo.chatTitle);
                chatImageView.setImageDrawable(AppCompatResources.getDrawable(context ,R.drawable.ic_group_show_chats));
            }else{
                personChatInfo personChatInfo = (personChatInfo) chatInfo;
                chatName.setText(personChatInfo.comradName);
                //Drawable drawable = AppCompatResources.getDrawable(context ,R.drawable.ic_person_show_chats);
                Picasso.get()
                        .load(personChatInfo.comradProfileImage)
                        .placeholder(R.drawable.ic_sync)
                        .into(chatImageView);
            }


            if(!TextUtils.equals(User.getUID(), chatInfo.getMessage().senderUID)) {
                lastMessageSenderName.setText(chatInfo.getMessage().senderName + ": ");
            }

            lastMessage.setText(chatInfo.getMessage().messageText);
            lastMessageTime.setText(new SimpleDateFormat("HH:mm").format(chatInfo.getMessage().messageSentTime));
        }
    }
}
