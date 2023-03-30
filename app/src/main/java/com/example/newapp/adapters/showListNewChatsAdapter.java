package com.example.newapp.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newapp.R;
import com.example.newapp.domain.models.chatModels.chatInfoForPossibleNewChat;
import com.example.newapp.interfaces.adapterOnClickInterface;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class showListNewChatsAdapter extends RecyclerView.Adapter<showListNewChatsAdapter.viewHolder> {

    private ArrayList<chatInfoForPossibleNewChat> list;
    private adapterOnClickInterface callback;

    private Context context;

    public showListNewChatsAdapter(ArrayList<chatInfoForPossibleNewChat> list, adapterOnClickInterface callback) {
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

        CircleImageView comradProfileImage;
        TextView comradName;
        TextView comradStatus;
        LinearLayout mainElem;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            mainElem = itemView.findViewById(R.id.mainElemShowListPossibleChats);
            comradName = itemView.findViewById(R.id.comradNameShowPossibleChats);
            comradStatus = itemView.findViewById(R.id.comradStatusShowPossibleChats);
            comradProfileImage = itemView.findViewById(R.id.comradProfileImageShowPossibleNewChats);
            mainElem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.callback(list.get(getAdapterPosition()));
                }
            });
        }

        public void bind(chatInfoForPossibleNewChat newChatData){
            comradName.setText(newChatData.comradName);
            if(newChatData.comradProfileMessageURL != null){
                Log.d("Aboba", newChatData.comradProfileMessageURL);
                Picasso.get()
                        .load(newChatData.comradProfileMessageURL)
                        .placeholder(R.drawable.ic_sync)
                        .into(comradProfileImage);
            }else{
                Drawable drawable = AppCompatResources.getDrawable(context, R.drawable.ic_person_show_chats);
                comradProfileImage.setImageDrawable(drawable);
            }
            if(newChatData.getComradLastTimeSeen() == null){
                comradStatus.setTextColor(ContextCompat.getColor(context, R.color.btnGreen));
                comradStatus.setText(newChatData.comradStatus);
            }else{
                comradStatus.setTextColor(ContextCompat.getColor(context, R.color.textStrongGrey));
                comradStatus.setText("был(а) в " + new SimpleDateFormat("HH:mm").format(newChatData.getComradLastTimeSeen()));
            }
        }
    }
}
