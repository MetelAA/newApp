package com.example.newapp.ui.chat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.newapp.R;
import com.example.newapp.adapters.showMessageRecViewAdapter;
import com.example.newapp.databinding.FragmentChatBinding;
import com.example.newapp.domain.models.chatModels.chatInfo;
import com.example.newapp.domain.models.chatModels.chatInfoForPossibleNewChat;
import com.example.newapp.domain.models.chatModels.chatMessage;
import com.example.newapp.domain.models.chatModels.createNewChatData;
import com.example.newapp.domain.models.chatModels.groupChatInfo;
import com.example.newapp.domain.models.chatModels.imageMessageForSend;
import com.example.newapp.domain.models.chatModels.message;
import com.example.newapp.domain.models.chatModels.personChatInfo;
import com.example.newapp.domain.models.chatModels.textMessageForSend;
import com.example.newapp.domain.models.chatModels.userStatus;
import com.example.newapp.global.User;
import com.example.newapp.global.constants;
import com.example.newapp.interfaces.adapterOnClickInterface;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class chatFragment extends Fragment {

    FragmentChatBinding binding;
    private FirebaseFirestore fStore;
    private chatViewModelImpl viewModel;
    private TextView chatTitle;
    private TextView comradStatus;
    private CircleImageView chatCircleImageView;
    private ImageButton backToSelectChat;
    private ImageButton attachFile;
    private EditText messageEditText;
    private ImageButton sendMsg;
    private RecyclerView showMsgRecyclerView;

    private showMessageRecViewAdapter adapter;
    private ConstraintLayout mainElem;

    private LinkedList<message> messagesList = new LinkedList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fStore = FirebaseFirestore.getInstance();
        binding = FragmentChatBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(chatViewModelImpl.class);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.darkerBluePrimary));
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mainElem = binding.mainElemChatFragment;
        chatTitle = binding.chatTitleTextViewChat;
        comradStatus = binding.comradStatusTextViewChat;
        backToSelectChat = binding.backToShowChatsChat;
        messageEditText = binding.messageEditTextChat;
        attachFile = binding.attachFileBtnChat;
        sendMsg = binding.sendMessageBtnChat;
        showMsgRecyclerView = binding.recyclerViewShowMessages;
        chatCircleImageView = binding.chatImageTitleImageView;


        String launchIntent = getArguments().getString("launchType");
        if (launchIntent != null && TextUtils.equals(launchIntent, "createNewChat")) {
            createNewChat();
        } else {
            chatInfo chatInfo = (chatInfo) getArguments().getSerializable("chatInfo");
            viewModel.setChatID(chatInfo.chatID); // устанавливаем ID чата для и отправки чтения сообщений
            if (TextUtils.equals(chatInfo.chatType, constants.KEY_CHAT_TYPE_EQUALS_GROUP_CHAT)) {
                groupChatInfo groupChatInfo = (groupChatInfo) chatInfo;
                showOverlayForGroupChat(groupChatInfo.chatTitle);
            } else {
                personChatInfo personChatInfo = (personChatInfo) chatInfo;
                showOverlayForPersonChat(personChatInfo.comradName, personChatInfo.comradProfileImage);
                getAndObserveComradStatus(personChatInfo.comradUID);
            }
            showMessages();
        }
        setObservers();
        setListeners();
        return binding.getRoot();
    }

    private void createNewChat() {
        chatInfoForPossibleNewChat chatInfoForPossibleNewChat = (chatInfoForPossibleNewChat) getArguments().getSerializable("newChatInfo");

        List<String> chatMembersUIDs = new ArrayList<>();
        chatMembersUIDs.add(User.getUID());
        chatMembersUIDs.add(chatInfoForPossibleNewChat.comradUID);
        viewModel.createNewChat(new createNewChatData(
                        constants.KEY_CHAT_TYPE_EQUALS_PERSON_CHAT,
                        chatMembersUIDs
                )
        );

        viewModel.onChatCreated.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String chatID) {
                viewModel.setChatID(chatID);
                getAndObserveComradStatus(chatInfoForPossibleNewChat.comradUID);
                showOverlayForPersonChat(chatInfoForPossibleNewChat.comradName, chatInfoForPossibleNewChat.comradProfileMessageURL);
                showMessages();
            }
        });

    }

    private void setListeners() {
        attachFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                getContentActivityResultLauncher.launch(intent);
            }
        });
        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageEditText.getText().toString();
                if (!TextUtils.isEmpty(messageText)) {
                    textMessageForSend message = new textMessageForSend(User.getName(), User.getUID(), new Date(), messageText);
                    viewModel.sendMessage(message);
                }
            }
        });
    }


    ActivityResultLauncher<Intent> getContentActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        imageMessageForSend message = new imageMessageForSend(User.getName(), User.getUID(), new Date(), result.getData().getData());
                        viewModel.sendMessage(message);
                    }
                }
            });


    private void setObservers() {
        viewModel.onErrorLiveData.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Snackbar.make(mainElem, s, Snackbar.ANIMATION_MODE_SLIDE).show();
            }
        });
    }


    private void showOverlayForPersonChat(String comradName, String comradProfileImage) {
        Picasso.get()
                .load(comradProfileImage)
                .placeholder(R.drawable.ic_sync)
                .into(chatCircleImageView);
        chatTitle.setText(comradName);
        comradStatus.setVisibility(View.VISIBLE);
    }

    private void showOverlayForGroupChat(String chatTitle) {
        Drawable drawable = AppCompatResources.getDrawable(getContext(), R.drawable.ic_group);
        chatCircleImageView.setImageDrawable(drawable);
        this.chatTitle.setText(chatTitle);
        comradStatus.setVisibility(View.INVISIBLE);
    }

    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

    private void getAndObserveComradStatus(String comradUID) {
        viewModel.getAndObserveComradStatus(comradUID);
        viewModel.comradStatus.observe(getViewLifecycleOwner(), new Observer<userStatus>() {
            @Override
            public void onChanged(userStatus userStatus) {
                if (TextUtils.equals(constants.KEY_USER_STATUS_EQUALS_OFFLINE, userStatus.userStatus)) {
                    comradStatus.setText("Был в " + dateFormat.format(userStatus.getLastTimeSeen()));
                } else {
                    comradStatus.setText(userStatus.userStatus);
                }
            }
        });
    }

    private void showMessages() {
        adapter = new showMessageRecViewAdapter(new adapterOnClickInterface() {
            @Override
            public void callback(Object data) {

            }
        });
        viewModel.getPreviousMessages(new Date());
        viewModel.getNewMessages(new Date());

        viewModel.previousMessages.observe(getViewLifecycleOwner(), new Observer<ArrayList<message>>() {
            @Override
            public void onChanged(ArrayList<message> messages) {
                adapter.addItemToFirstPosition(messages);
            }
        });
        viewModel.newMessages.observe(getViewLifecycleOwner(), new Observer<ArrayList<message>>() {
            @Override
            public void onChanged(ArrayList<message> messages) {
                adapter.addItemsToBack(messages);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        showMsgRecyclerView.setLayoutManager(layoutManager);
        showMsgRecyclerView.setHasFixedSize(true);
        showMsgRecyclerView.setAdapter(adapter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        viewModel.removeListeners();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}