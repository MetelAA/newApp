package com.example.newapp.adapters;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newapp.R;
import com.example.newapp.domain.models.chatModels.chatMessageWithImage;
import com.example.newapp.domain.models.chatModels.chatMessageWithText;
import com.example.newapp.domain.models.chatModels.message;
import com.example.newapp.global.User;
import com.example.newapp.global.constants;
import com.example.newapp.interfaces.adapterOnClickInterface;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class showMessageRecViewAdapter extends RecyclerView.Adapter<showMessageRecViewAdapter.viewHolder> {

    adapterOnClickInterface callback;

    public showMessageRecViewAdapter(adapterOnClickInterface callback) {
        this.callback = callback;
    }

    LinkedList<message> messagesList = new LinkedList<>();

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_for_message, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        if(TextUtils.equals(messagesList.get(position).senderUID, User.getUID())){
            holder.bindOutgoingMessage(messagesList.get(position));
        }else{
            holder.bindIncomingMessage(messagesList.get(position));
        }

        //if(messagesList.size() >= position - 7) callback.callback(messagesList.getLast().sentTime);
        //Log.d("Aboba", "bind        showList");
        //Log.d("Aboba", messagesList.get(position).toString() + "   position");
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public void addItemToFirstPosition(List<message> list){
        messagesList.addAll(0, list);
        notifyDataSetChanged();
    }

    public void addItemsToBack(message msg){
        messagesList.add(messagesList.size(),msg);
        notifyItemInserted(messagesList.size());
    }

    class viewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout incomingMessageLayout;
        private final LinearLayout outgoingMessageLayout;

        private final TextView incomingMessageText;
        private final TextView incomingMessageSentTime;
        private final TextView incomingMessageSenderName;
        private final ImageView incomingMessageImageView;


        private final TextView outgoingMessageText;
        private final TextView outgoingMessageSentTime;
        private final TextView outgoingMessageSenderName;
        private final ImageView outgoingMessageImageView;
        public viewHolder(@NonNull View view) {
            super(view);
            incomingMessageLayout = view.findViewById(R.id.incomingMessageLayout);
            incomingMessageText = view.findViewById(R.id.incomingMessageText);
            incomingMessageSentTime = view.findViewById(R.id.incomingMessageSentTime);
            incomingMessageSenderName = view.findViewById(R.id.incomingMessageSenderName);
            incomingMessageImageView = view.findViewById(R.id.incomingMessageImage);


            outgoingMessageLayout = view.findViewById(R.id.outgoingMessageLayout);
            outgoingMessageText = view.findViewById(R.id.outgoingMessageText);
            outgoingMessageSentTime = view.findViewById(R.id.outgoingMessageSentTime);
            outgoingMessageSenderName = view.findViewById(R.id.outgoingMessageSenderName);
            outgoingMessageImageView = view.findViewById(R.id.outgoingMessageImage);
        }
        public void bindOutgoingMessage(message message) {
            incomingMessageLayout.setVisibility(View.GONE);
            outgoingMessageLayout.setVisibility(View.VISIBLE);
            showOutgoingMessageBaseData(message);
            if(message instanceof chatMessageWithText){
                chatMessageWithText textMessage = (chatMessageWithText) message;
                outgoingMessageText.setVisibility(View.VISIBLE);
                outgoingMessageText.setText(textMessage.messageText);
                outgoingMessageImageView.setVisibility(View.GONE);
            }else{
                chatMessageWithImage imageMessage = (chatMessageWithImage) message;
                outgoingMessageText.setVisibility(View.GONE);
                outgoingMessageImageView.setVisibility(View.VISIBLE);
                Picasso.get()
                        .load(imageMessage.messageImageURL)
                        .placeholder(R.drawable.ic_sync)
                        .into(outgoingMessageImageView);
            }
        }

        public void bindIncomingMessage(message message){
            outgoingMessageLayout.setVisibility(View.GONE);
            incomingMessageLayout.setVisibility(View.VISIBLE);
            showIncomingMessageBaseData(message);
            if(message instanceof chatMessageWithText){
                chatMessageWithText textMessage = (chatMessageWithText) message;
                incomingMessageText.setVisibility(View.VISIBLE);
                incomingMessageText.setText(textMessage.messageText);
                incomingMessageImageView.setVisibility(View.GONE);
            }else{
                chatMessageWithImage imageMessage = (chatMessageWithImage) message;
                incomingMessageText.setVisibility(View.GONE);
                incomingMessageImageView.setVisibility(View.VISIBLE);
                Picasso.get()
                        .load(imageMessage.messageImageURL)
                        .placeholder(R.drawable.ic_sync)
                        .into(incomingMessageImageView);
            }
        }

        private void showIncomingMessageBaseData(message message){
            incomingMessageSenderName.setText(message.senderName);
            incomingMessageSentTime.setText(normSimpleDate(message.messageSentTime));
        }
        private void showOutgoingMessageBaseData(message message){
            outgoingMessageSenderName.setText(message.senderName);
            outgoingMessageSentTime.setText(normSimpleDate(message.messageSentTime));
        }

        private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        public String normSimpleDate(Date date){
            return simpleDateFormat.format(date);
        }
    }
}
