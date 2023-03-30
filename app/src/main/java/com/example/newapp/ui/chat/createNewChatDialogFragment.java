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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.newapp.R;
import com.example.newapp.adapters.showEmptyMessageRecViewAdapter;
import com.example.newapp.adapters.showListNewChatsAdapter;
import com.example.newapp.databinding.FragmentCreateNewChatBinding;
import com.example.newapp.domain.models.chatModels.chatInfoForPossibleNewChat;
import com.example.newapp.global.User;
import com.example.newapp.interfaces.adapterOnClickInterface;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class createNewChatDialogFragment extends Fragment {

    private FragmentCreateNewChatBinding binding;

    private createNewChatViewModelImpl viewModel;
    private ConstraintLayout mainElem;
    private RecyclerView recyclerView;
    private ImageButton returnToShowChats;

    private ArrayList<String> existingChatList;
    private ArrayList<chatInfoForPossibleNewChat> chatInfoForPossibleNewChats = new ArrayList<>();



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCreateNewChatBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(createNewChatViewModelImpl.class);

        mainElem = binding.mainElemCreateNewChat;
        recyclerView = binding.recyclerViewCreateNewChat;
        returnToShowChats = binding.returnToShowChats;

        existingChatList = getArguments().getStringArrayList("existingChatsMembersUIDs");
        existingChatList.add(User.getUID());

        setObservers();
        showPossibleNewChats();
        return binding.getRoot();
    }


    private void showPossibleNewChats() {
        showListNewChatsAdapter newChatsAdapter = new showListNewChatsAdapter(chatInfoForPossibleNewChats, new adapterOnClickInterface() {
            @Override
            public void callback(Object chatInfoForNewChat) {
                Bundle bundle = new Bundle();
                bundle.putString("launchType", "createNewChat");
                bundle.putSerializable("newChatInfo", (chatInfoForPossibleNewChat) chatInfoForNewChat);
                chatInfoForPossibleNewChats.clear();
                Navigation.findNavController(mainElem).navigate(R.id.action_createNewChatDialogFragment_to_chatFragment, bundle);
            }
        });
        viewModel.getPossibleNewChats(existingChatList);
        viewModel.onGotPossibleChats.observe(getViewLifecycleOwner(), new Observer<ArrayList<chatInfoForPossibleNewChat>>() {
            @Override
            public void onChanged(ArrayList<chatInfoForPossibleNewChat> pieceChatInfoForPossibleNewChats) {
                chatInfoForPossibleNewChats.addAll(pieceChatInfoForPossibleNewChats);
                //Log.d("Aboba", "mainList after change -----" + chatInfoForNewChats);
                //newChatsAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(newChatsAdapter);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        if (chatInfoForPossibleNewChats.isEmpty()) {
            showEmptyMessageRecViewAdapter emptyAdapter = new showEmptyMessageRecViewAdapter();
            recyclerView.setAdapter(emptyAdapter);
        }
    }

    private void setObservers() {
        viewModel.onErrorLiveData.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Snackbar.make(mainElem, s, Snackbar.ANIMATION_MODE_SLIDE).show();
            }
        });
    }

}