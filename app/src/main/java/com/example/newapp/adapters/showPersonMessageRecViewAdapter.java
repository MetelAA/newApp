package com.example.newapp.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newapp.R;
import com.example.newapp.core.User;
import com.example.newapp.core.textMessagePersonChat;
import com.example.newapp.interfaces.CallbackWithInt;
import com.example.newapp.interfaces.MessageAdapterCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class showPersonMessageRecViewAdapter extends RecyclerView.Adapter<showPersonMessageRecViewAdapter.viewHolder> {

    MessageAdapterCallback callback;

    public showPersonMessageRecViewAdapter(MessageAdapterCallback callback) {
        this.callback = callback;
    }

    LinkedList<textMessagePersonChat> messagesList = new LinkedList<>();

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_for_text_message, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        textMessagePersonChat message = messagesList.get(position);
        if(TextUtils.equals(message.senderUID, User.getUID())){
            holder.bindOutgoingMessage(message);
        }else{
            holder.bindIncomingMessage(message);
        }
        if(messagesList.size() >= position - 7) callback.callback(messagesList.getLast().sentTime);

    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public void addItemToFirstPosition(List<textMessagePersonChat> list){
        messagesList.addAll(0, list);
        notifyDataSetChanged();
    }

    public void addItemsToBack(List<textMessagePersonChat> list){
        messagesList.addAll(messagesList.size(),list);
        notifyDataSetChanged();
    }

    class viewHolder extends RecyclerView.ViewHolder {
        private LinearLayout incomingMessage;
        private LinearLayout outgoingMessage;

        private TextView messageTextIncomingMessage;
        private TextView msgSentTimeIncomingMessage;
        private TextView msgSenderNameIncomingMessage;

        private TextView messageTextOutgoingMessage;
        private TextView msgSentTimeOutgoingMessage;
        private TextView msgSenderNameOutgoingMessage;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            incomingMessage = itemView.findViewById(R.id.layoutForIncomingMessage);
            outgoingMessage = itemView.findViewById(R.id.layoutForOutgoingMessage);

            messageTextIncomingMessage = itemView.findViewById(R.id.messageTextTextViewIncomingMessage);
            msgSentTimeIncomingMessage = itemView.findViewById(R.id.messageSentTimeTextViewIncomingMessage);
            msgSenderNameIncomingMessage = itemView.findViewById(R.id.messageSenderNameTextViewIncomingMessage);

            messageTextOutgoingMessage = itemView.findViewById(R.id.messageTextTextViewOutgoingMessage);
            msgSentTimeOutgoingMessage = itemView.findViewById(R.id.messageSentTimeTextViewOutgoingMessage);
            msgSenderNameOutgoingMessage = itemView.findViewById(R.id.messageSenderNameTextViewOutgoingMessage);
        }

        public void bindIncomingMessage(textMessagePersonChat message){
            incomingMessage.setVisibility(View.VISIBLE);
            outgoingMessage.setVisibility(View.GONE);

            messageTextIncomingMessage.setText(message.message);
            msgSentTimeIncomingMessage.setText(normSimpleDate(message.sentTime));
            msgSenderNameIncomingMessage.setVisibility(View.GONE);
        }

        public void bindOutgoingMessage(textMessagePersonChat message){
            outgoingMessage.setVisibility(View.VISIBLE);
            incomingMessage.setVisibility(View.GONE);

            messageTextOutgoingMessage.setText(message.message);
            msgSentTimeOutgoingMessage.setText(normSimpleDate(message.sentTime));
            msgSenderNameOutgoingMessage.setVisibility(View.GONE);
        }
        private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        public String normSimpleDate(Date date){
            return simpleDateFormat.format(date);
        }
    }
}
