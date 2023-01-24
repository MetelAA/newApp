package com.example.newapp.ui.screensForChat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.newapp.R;
import com.example.newapp.adapters.AdapterShowListNewChats;
import com.example.newapp.core.chatInfoForShowChats;
import com.example.newapp.core.db.getDeleteGroupUsers;
import com.example.newapp.databinding.FragmentCreateNewChatBinding;
import com.example.newapp.interfaces.CallbackInterfaceWithList;
import com.example.newapp.interfaces.RecyclerViewOnClickCallback;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class createNewChatFragment extends Fragment {

    private FragmentCreateNewChatBinding binding;
    private FirebaseFirestore fStore;
    private ConstraintLayout mainElem;
    private RecyclerView recyclerView;
    private ImageButton returnToShowChats;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { //это пиз*** а не метод слишком дох** запросов к бд я бл* запутался
        fStore = FirebaseFirestore.getInstance();
        binding = FragmentCreateNewChatBinding.inflate(inflater, container, false);

        mainElem = binding.mainElemCreateNewChat;
        recyclerView = binding.recyclerViewCreateNewChat;
        returnToShowChats = binding.returnToShowChats;


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getPossibleNewChats();

        returnToShowChats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_createNewChatFragment_to_fragment_show_chats2);
            }
        });
    }

    private void getPossibleNewChats() {

    }

    private void showPossibleNewChats(ArrayList list){
        AdapterShowListNewChats adapter = new AdapterShowListNewChats(list, );

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void creatingNewChat(String comradUID, String comradName){

    }
}