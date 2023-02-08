package com.example.newapp.ui.chat;

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
import com.example.newapp.adapters.showChatListRecViewAdapter;
import com.example.newapp.domain.models.oldModels.chatInfoForShowChats;
import com.example.newapp.data.chat.getRealChatsList;
import com.example.newapp.databinding.FragmentShowChatsBinding;
import com.example.newapp.interfaces.CallbackInterfaceWithList;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class showChatsFragment extends Fragment {

    FragmentShowChatsBinding binding;
    RecyclerView recyclerView;
    ConstraintLayout mainElem;
    FirebaseFirestore fStore;
    ImageButton addNewChats;

    ArrayList<chatInfoForShowChats> chatList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fStore = FirebaseFirestore.getInstance();

        binding = FragmentShowChatsBinding.inflate(inflater, container, false);
        recyclerView = binding.recyclerViewShowChats;
        mainElem = binding.mainElemShowChats;
        addNewChats = binding.addNewChatBtnShowChats;

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showChats();


        addNewChats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("key", chatList); //прочитать про эту щнягу
                Navigation.findNavController(view).navigate(R.id.action_fragment_show_chats_to_createNewChatFragment2);
            }
        });
    }




    private void showChats(){
        getRealChatsList getRealChatsList = new getRealChatsList(new CallbackInterfaceWithList() {
            @Override
            public void requestResult(ArrayList list) {
                chatList = list;
                showChatListRecViewAdapter adapter = new showChatListRecViewAdapter(chatList);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void throwError(String error) {
                Snackbar.make(mainElem, "Ошибка! Не удалось получть чаты", Snackbar.LENGTH_LONG).show();
            }
        });
        getRealChatsList.getChatList(fStore);
    }
}