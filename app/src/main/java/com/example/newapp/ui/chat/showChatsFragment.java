package com.example.newapp.ui.chat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;


import com.example.newapp.MainActivity;
import com.example.newapp.R;
import com.example.newapp.adapters.showChatsListRecViewAdapter;
import com.example.newapp.adapters.showEmptyMessageRecViewAdapter;
import com.example.newapp.domain.models.chatModels.chatInfo;
import com.example.newapp.databinding.FragmentShowChatsBinding;
import com.example.newapp.domain.models.chatModels.chatInfoWithSnapshotStatus;
import com.example.newapp.domain.models.chatModels.personChatInfo;
import com.example.newapp.domain.models.comparators.chatInfoComparator;
import com.example.newapp.global.constants;
import com.example.newapp.interfaces.adapterOnClickInterface;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;


public class showChatsFragment extends Fragment {

    private ConstraintLayout mainElem;
    private FragmentShowChatsBinding binding;
    private showChatsViewModelImpl viewModel;
    private RecyclerView recyclerView;

    private ImageButton addNewChats;

    private showChatsListRecViewAdapter showChatsAdapter;
    ArrayList<chatInfo> chatList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentShowChatsBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(showChatsViewModelImpl.class);
        recyclerView = binding.recyclerViewShowChats;
        mainElem = binding.mainElemShowChats;
        addNewChats = binding.addNewChatBtnShowChats;


        showChats();

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addNewChats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> existingChatsMembersUIDs = new ArrayList<>();
                for (int i = 0; i < chatList.size(); i++) {
                    if(TextUtils.equals(chatList.get(i).chatType, constants.KEY_CHAT_TYPE_EQUALS_PERSON_CHAT)){
                        personChatInfo personChatInfo = (personChatInfo) chatList.get(i);
                        existingChatsMembersUIDs.add(personChatInfo.comradUID);
                    }
                }
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("existingChatsMembersUIDs", existingChatsMembersUIDs);
                Navigation.findNavController(view).navigate(R.id.action_fragment_show_chats_to_createNewChatDialogFragment, bundle);
            }
        });
    }




    private void showChats(){

        if(!viewModel.isListenerActiveFlag){
            viewModel.getExistingChats();
            showChatsAdapter = new showChatsListRecViewAdapter(chatList, new adapterOnClickInterface() {
                @Override
                public void callback(Object data) {
                    chatInfo chatInfo = (chatInfo) data;
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("chatInfo" ,chatInfo);
                    Navigation.findNavController(mainElem).navigate(R.id.action_fragment_show_chats_to_chatFragment, bundle);
                }
            });
            showEmptyMessageRecViewAdapter showEmptyMessage = new showEmptyMessageRecViewAdapter();
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(showEmptyMessage);
        }

        for (int i = 0; i < viewModel.chatInfosList.size() - 1; i++) {
            chatList.add(viewModel.chatInfosList.get(i));
        }
        viewModel.gotListExitingChats.observe(getViewLifecycleOwner(), new Observer<chatInfoWithSnapshotStatus>() {
            @Override
            public void onChanged(chatInfoWithSnapshotStatus chatInfoWithSnapshotStatus) {
                Log.d("Aboba", "onChanged");
                switch (chatInfoWithSnapshotStatus.changeType) {
                    case ADDED:
                        Log.d("Aboba", "chat added " + chatInfoWithSnapshotStatus.chatInfo.chatID);
                        chatList.add(chatInfoWithSnapshotStatus.chatInfo);
                        viewModel.chatInfosList.add(chatInfoWithSnapshotStatus.chatInfo);
                        break;
                    case MODIFIED:
                        Log.d("Aboba", "chat modified" + chatInfoWithSnapshotStatus.chatInfo.chatID);
                        chatInfo changedChat = chatList.stream()
                                .filter(chat -> chatInfoWithSnapshotStatus.chatInfo.chatID.equals(chat.chatID))
                                .findAny()
                                .orElse(null);
                        Log.d("Aboba", "changed chat - " + changedChat);
                        if(changedChat == null){
                            chatList.add(chatInfoWithSnapshotStatus.chatInfo);
                        }else{
                            chatList.set(chatList.indexOf(changedChat), chatInfoWithSnapshotStatus.chatInfo);
                        }
                        break;
                    case REMOVED:
                        Log.d("Aboba", "chat rewmoved" + chatInfoWithSnapshotStatus.chatInfo.chatID);
                        chatInfo removingChat = chatList.stream()
                                .filter(chat -> chatInfoWithSnapshotStatus.chatInfo.chatID.equals(chat.chatID))
                                .findAny()
                                .orElse(null);
                        chatList.remove(removingChat);
                        viewModel.chatInfosList.remove(removingChat);
                        break;
                }
                chatList.sort(new chatInfoComparator());
                Log.d("Aboba", chatList.toString());
                if((!chatList.isEmpty()) && recyclerView.getAdapter() instanceof showEmptyMessageRecViewAdapter) {
                    recyclerView.setAdapter(showChatsAdapter);
                }
            }
        });



        viewModel.onErrorLiveData.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Snackbar.make(mainElem, s, Snackbar.ANIMATION_MODE_SLIDE).show();
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Aboba", "onDestroy");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("Aboba", "onStop");
        chatList.clear();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("Aboba", showChatsAdapter + "  adapter start");
    }
}