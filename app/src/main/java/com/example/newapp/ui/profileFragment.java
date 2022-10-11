package com.example.newapp.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.newapp.Login;
import com.example.newapp.R;
import com.example.newapp.core.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class profileFragment extends Fragment {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;



    private ImageButton profileImg;
    private TextView profileName;
    private TextView profileEmail;
    private TextView nameGroup;
    private Button optionGroupBtn;
    private ImageButton logOutBtn;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        View v = inflater.inflate(R.layout.fragment_profile, container, false);


        profileImg = v.findViewById(R.id.profileFragment);
        profileName = v.findViewById(R.id.nameText);
        profileEmail = v.findViewById(R.id.emailText);
        nameGroup = v.findViewById(R.id.nameGroupText);

        profileImg = v.findViewById(R.id.profileImage);
        optionGroupBtn = v.findViewById(R.id.optionGroupBtn);
        logOutBtn = v.findViewById(R.id.logOutBtn);


        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Aboba", " huita");
            }
        });


        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                startActivity(new Intent(getActivity(), Login.class));
                getActivity().finish();
                //придумать это
            }


        });


        optionGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        showProfileData();

        return v;
    }



    private void showProfileData(){
        profileName.setText(User.getName());
        profileEmail.setText(User.getEmail());
        nameGroup.setText(User.getUser().getGroupName());
    }

}