package com.example.newapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newapp.R;
import com.example.newapp.domain.models.chatModels.chatInfoForNewChat;
import com.example.newapp.interfaces.adapterOnClickInterface;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class showListNewChatsAdapter extends RecyclerView.Adapter<showListNewChatsAdapter.viewHolder> {

    private ArrayList<chatInfoForNewChat> list;
    private adapterOnClickInterface callback;

    private Context context;

    public showListNewChatsAdapter(ArrayList<chatInfoForNewChat> list, adapterOnClickInterface callback) {
        this.list = list;
        this.callback = callback;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_for_show_list_possible_chats, parent, false);
        context = parent.getContext();
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
        TextView comradName;
        TextView comradStatus;
        LinearLayout mainElem;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            mainElem = itemView.findViewById(R.id.mainElemShowListPossibleChats);
            comradName = itemView.findViewById(R.id.comradNameShowPossibleChats);
            comradStatus = itemView.findViewById(R.id.comradStatusShowPossibleChats);
            mainElem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.callback(list.get(getAdapterPosition()));
                }
            });
        }

        public void bind(chatInfoForNewChat newChatData){
            Log.d("Aboba", newChatData.toString());
            comradName.setText(newChatData.Name);
            if(newChatData.getLastTimeSeen() == null){
                comradStatus.setTextColor(ContextCompat.getColor(context, R.color.btnGreen));
                comradStatus.setText(newChatData.Status);
            }else{
                comradStatus.setTextColor(ContextCompat.getColor(context, R.color.textStrongGrey));
                comradStatus.setText("был(а) в " + new SimpleDateFormat("HH:mm").format(newChatData.getLastTimeSeen()));
            }
        }
    }
}
