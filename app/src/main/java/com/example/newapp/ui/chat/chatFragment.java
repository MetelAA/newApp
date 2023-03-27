package com.example.newapp.ui.chat;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.newapp.R;
import com.example.newapp.adapters.showPersonMessageRecViewAdapter;
import com.example.newapp.data.chat.getMessages;
import com.example.newapp.databinding.FragmentChatBinding;
import com.example.newapp.domain.models.chatModels.chatInfo;
import com.example.newapp.domain.models.chatModels.chatMessage;
import com.example.newapp.domain.models.chatModels.groupChatInfo;
import com.example.newapp.domain.models.chatModels.personChatInfo;
import com.example.newapp.global.constants;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;


public class chatFragment extends Fragment {

    FragmentChatBinding binding;
    private FirebaseFirestore fStore;
    private chatViewModelImpl viewModel;
    private TextView comradName;
    private TextView comradStatus;
    private ImageButton backToSelectChat;
    private ImageButton attachFile;
    private ImageButton sendMsg;
    private RecyclerView showMsgRecyclerView;

    private ConstraintLayout mainElem;

    private ArrayList<chatMessage> chatMessages = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fStore = FirebaseFirestore.getInstance();
        binding = FragmentChatBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(chatViewModelImpl.class);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.darkerBluePrimary));
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mainElem = binding.mainElemChatFragment;
        comradName = binding.comradNameTextViewChat;
        comradStatus = binding.comradStatusTextViewChat;
        backToSelectChat = binding.backToShowChatsChat;
        attachFile = binding.attachFileBtnChat;
        sendMsg = binding.sendMessageBtnChat;
        showMsgRecyclerView = binding.recyclerViewShowMessages;

        chatInfo chatInfo = (chatInfo) getArguments().getSerializable("chatInfo");
        if(TextUtils.equals(chatInfo.chatType, constants.KEY_CHAT_TYPE_EQUALS_GROUP_CHAT)){
            groupChatInfo groupChatInfo = (groupChatInfo) chatInfo;
            Log.d("Aboba", groupChatInfo.toString());
        }else{
            personChatInfo personChatInfo = (personChatInfo) chatInfo;
            Log.d("Aboba", personChatInfo.toString());
        }

        setObservers();

        return binding.getRoot();
    }

    private void setObservers() {
        viewModel.onErrorLiveData.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Snackbar.make(mainElem, s, Snackbar.ANIMATION_MODE_SLIDE).show();
            }
        });
    }


    private void showMessages(){

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}