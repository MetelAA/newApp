package com.example.newapp.ui.screensForChat;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.newapp.R;
import com.example.newapp.databinding.FragmentChatBinding;

public class chatFragment extends Fragment {

    FragmentChatBinding binding;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(getLayoutInflater());





        return binding.getRoot();
    }
}