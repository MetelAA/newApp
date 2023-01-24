package com.example.newapp.ui.screensForChat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.newapp.R;
import com.example.newapp.adapters.showPersonMessageRecViewAdapter;
import com.example.newapp.core.db.chat.getMessages;
import com.example.newapp.databinding.FragmentChatBinding;
import com.example.newapp.interfaces.CallbackInterfaceWithList;
import com.example.newapp.interfaces.MessageAdapterCallback;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;


public class chatFragment extends Fragment {

    FragmentChatBinding binding;
    private FirebaseFirestore fStore;

    private TextView comradName;
    private TextView comradStatus;
    private ImageButton backToSelectChat;
    private ImageButton attachFile;
    private ImageButton sendMsg;
    private RecyclerView showMsgRecyclerView;

    private ConstraintLayout mainElem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fStore = FirebaseFirestore.getInstance();
        binding = FragmentChatBinding.inflate(inflater, container, false);


        mainElem = binding.mainElemChatFragment;
        comradName = binding.comradNameTextViewChat;
        comradStatus = binding.comradStatusTextViewChat;
        backToSelectChat = binding.backToShowChatsChat;
        attachFile = binding.attachFileBtnChat;
        sendMsg = binding.sendMessageBtnChat;
        showMsgRecyclerView = binding.recyclerViewShowMessages;

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showMessages();
    }


    @Override
    public void onPause() {
        super.onPause();

}

    private getMessages getMessages;
    private showPersonMessageRecViewAdapter adapter;

    public void showMessages(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        getMessages = new getMessages(fStore, "",
                new CallbackInterfaceWithList() { //getNewMassageList
                    @Override
                    public void requestResult(ArrayList list) {
                        adapter.addItemsToBack(list);
                    }

                    @Override
                    public void throwError(String error) {
                        Snackbar.make(mainElem, error, Snackbar.LENGTH_LONG).show();
                    }
                },
                new CallbackInterfaceWithList() { //setListener
                    @Override
                    public void requestResult(ArrayList list) {
                        adapter.addItemToFirstPosition(list);
                    }

                    @Override
                    public void throwError(String error) {
                        Snackbar.make(mainElem, error, Snackbar.LENGTH_LONG).show();
                    }
                });
        getMessages.getListMessages(new Date(), 70, null);
        getMessages.setRealTimesUpdates();
        adapter = new showPersonMessageRecViewAdapter(new MessageAdapterCallback() {
            @Override
            public void callback(Date date) {
                getMessages.getListMessages(date, 70, null);
            }
        });
        showMsgRecyclerView.setLayoutManager(layoutManager);
        showMsgRecyclerView.setAdapter(adapter);
    }
}