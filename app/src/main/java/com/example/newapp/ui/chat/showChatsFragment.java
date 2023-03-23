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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


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

    ConstraintLayout mainElem;
    FragmentShowChatsBinding binding;
    showChatsViewModelImpl viewModel;
    RecyclerView recyclerView;

    ImageButton addNewChats;

    createNewChatDialogFragment dialogFragment;

    ArrayList<chatInfo> chatList = new ArrayList<>();


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
                chatList.clear();
            }
        });
    }




    private void showChats(){
        showChatsListRecViewAdapter showChatsAdapter = new showChatsListRecViewAdapter(chatList, new adapterOnClickInterface() {
            @Override
            public void callback(Object data) {

            }
        });
        showEmptyMessageRecViewAdapter showEmptyMessage = new showEmptyMessageRecViewAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(showChatsAdapter);

        if(!viewModel.isListenerActiveFlag){
            viewModel.getExistingChats();
        }
        viewModel.gotListExitingChats.observe(getViewLifecycleOwner(), new Observer<ArrayList<chatInfoWithSnapshotStatus>>() {
            @Override
            public void onChanged(ArrayList<chatInfoWithSnapshotStatus> chatInfoChangesList) {

                for (int i = 0; i < chatInfoChangesList.size(); i++) {
                    chatInfoWithSnapshotStatus chatInfoWithSnapshotStat = chatInfoChangesList.get(i);
                    switch (chatInfoWithSnapshotStat.changeType) {
                        case ADDED:
                            chatList.add(chatInfoWithSnapshotStat.chatInfo);
                            break;
                        case MODIFIED:
                            chatInfo changedChat = chatList.stream()
                                .filter(chat -> chatInfoWithSnapshotStat.chatInfo.chatID.equals(chat.chatID))
                                .findAny()
                                .orElse(null);
                            if(changedChat == null){
                                chatList.add(chatInfoWithSnapshotStat.chatInfo);
                            }else{
                                chatList.set(chatList.indexOf(changedChat), chatInfoWithSnapshotStat.chatInfo);
                            }
                            break;
                        case REMOVED:
                            chatInfo removingChat = chatList.stream()
                                    .filter(chat -> chatInfoWithSnapshotStat.chatInfo.chatID.equals(chat.chatID))
                                    .findAny()
                                    .orElse(null);
                            chatList.remove(removingChat);
                            break;
                    }
                }
                chatList.sort(new chatInfoComparator());
                if(chatList.isEmpty()){
                    recyclerView.setAdapter(showEmptyMessage);
                }else{
                    showChatsAdapter.notifyDataSetChanged();
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

}