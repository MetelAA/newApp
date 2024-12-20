package com.example.newapp.ui.profile;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.customPopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.example.newapp.R;

import com.example.newapp.adapters.AdapterListUsers;
import com.example.newapp.databinding.FragmentProfileBinding;
import com.example.newapp.domain.models.groupUserData;
import com.example.newapp.domain.models.profileDataAboutGroup;
import com.example.newapp.global.User;
import com.example.newapp.interfaces.adapterOnClickInterface;
import com.example.newapp.ui.login.Login;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Request;
import okhttp3.Response;

public class profileFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {


    private FragmentProfileBinding binding;

    private Button userOptionGroupBtn;
    private Button showGroupUsersListBtn;
    private CircleImageView profileImageView;

    private ConstraintLayout mainElem;

    private TextView profileNameTextView;
    private TextView profileEmailTextView;
    private TextView profileNameGroupTextView;
    private TextView profileNumberGroupTextView;

    private profileViewModelImpl viewModel;

    private AdapterListUsers AdapterListUsers;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(profileViewModelImpl.class);


        profileImageView = binding.profileImageView;
        profileNameTextView = binding.profileNameTextView;

        userOptionGroupBtn = binding.btnProfileOptionGroup;

        showGroupUsersListBtn = binding.groupUserListProfileBtn;
        profileEmailTextView = binding.profileEmailTextView;
        profileNameGroupTextView = binding.profileNameGroupTextView;
        profileNumberGroupTextView = binding.profileNumberGroupTextView;

        mainElem = binding.mainElemProfile;

        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.profileToolbar);


        binding.profileToolbarMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customPopupMenu popupMenu = new customPopupMenu(getContext(), view);
                popupMenu.setOnMenuItemClickListener(profileFragment.this::onMenuItemClick);
                popupMenu.inflate(R.menu.profile_toolbar_menu);
                popupMenu.show();
            }
        });
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setObservers();
        showProfileUserData();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Log.d("Aboba", item.toString());
        switch (item.getItemId()){
            case R.id.changeProfilePhotoOption:
                doImageForUserProfile();
                return true;
            case R.id.logOutOption:
                logOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void doImageForUserProfile(){
        CropImageOptions cropImageOptions = new CropImageOptions();
        cropImageOptions.imageSourceIncludeGallery = true;
        cropImageOptions.imageSourceIncludeCamera = true;
        CropImageContractOptions cropImageContractOptions = new CropImageContractOptions(null, cropImageOptions);
        cropImageOptions.fixAspectRatio = true;
        cropImageOptions.outputRequestSizeOptions = CropImageView.RequestSizeOptions.RESIZE_FIT;
        cropImageOptions.outputRequestWidth = 800;
        cropImageOptions.outputRequestHeight = 800;
        cropImageOptions.cropShape = CropImageView.CropShape.OVAL;
        cropImage.launch(cropImageContractOptions);
    }
    ActivityResultLauncher<CropImageContractOptions> cropImage = registerForActivityResult(new CropImageContract(), result -> {
        if (result.isSuccessful()) {
            Log.d("Aboba", "viewModel.setUserProfileImage");
            viewModel.setUserProfileImage(result.getUriContent());
        }
    });

    private void setObservers(){
        viewModel.onErrorLiveData.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Snackbar.make(mainElem, s, Snackbar.ANIMATION_MODE_SLIDE).show();
            }
        });
        viewModel.onGotProfileGroupDataLiveData.observe(getViewLifecycleOwner(), new Observer<profileDataAboutGroup>() {
            @Override
            public void onChanged(profileDataAboutGroup profileDataAboutGroup) {
                showUserInGroupOptions(profileDataAboutGroup);
            }
        });
        viewModel.userProfileImageSet.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d("Aboba", "onUpdate prfoileImage Fragment    user.getPhptpURL " + User.getUser().getUserProfilePhotoUrl());
                showProfileUserData();
            }
        });
    }

    private void getProfileGroupData() {
        viewModel.initProfileGroupData();
    }

    private void showProfileUserData(){
        profileNameTextView.setText(User.getName());
        profileEmailTextView.setText(User.getEmail());
        if(User.getUser().getUserProfilePhotoUrl() != null){
            Picasso.get().setLoggingEnabled(true);
            Picasso.get().cancelRequest(profileImageView);
            Picasso.get().setIndicatorsEnabled(true);
            Picasso.get()
                    .load(User.getUser().getUserProfilePhotoUrl())
                    .placeholder(R.drawable.ic_sync)
                    .into(profileImageView);
        }else{
            Drawable drawable = AppCompatResources.getDrawable(getContext(), R.drawable.ic_person_show_chats);
            profileImageView.setImageDrawable(drawable);
        }

        if (!TextUtils.isEmpty(User.getUser().getGroupKey())) {
            getProfileGroupData();
        } else {
            showUserWithOutGroupOptions();
        }

        if (TextUtils.equals(User.getType(), "Учитель")) {
            showGroupUsersListBtn.setVisibility(View.VISIBLE);
            userOptionGroupBtn.setVisibility(View.INVISIBLE);
            groupUserManagement();
        } else {
            showGroupUsersListBtn.setVisibility(View.INVISIBLE);
        }
    }

    private void showUserInGroupOptions(profileDataAboutGroup profileDataAboutGroup){
        profileNameGroupTextView.setText(profileDataAboutGroup.groupName);
        Drawable drawableExit = AppCompatResources.getDrawable(getContext(), R.drawable.btn_style_red);
        userOptionGroupBtn.setText("Выйти из группы");
        userOptionGroupBtn.setBackground(drawableExit);
        exitGroupAlertDialogListener();
        showNumberOfUsers(profileDataAboutGroup.countGroupUsers);
    }

    private void showUserWithOutGroupOptions(){
        profileNameGroupTextView.setText("");
        Drawable drawableJoin = AppCompatResources.getDrawable(getContext(), R.drawable.btn_style_blue_primary);
        userOptionGroupBtn.setText("Присоединиться к группе");
        userOptionGroupBtn.setBackground(drawableJoin);
        profileNumberGroupTextView.setText("");
        joinGroupAlertDialogListener();
    }

    private void showNumberOfUsers(long numberOfUsers){
        if(numberOfUsers == 1){
            profileNumberGroupTextView.setText(numberOfUsers + " пользователь");
        }else if(numberOfUsers > 1 && numberOfUsers <=4){
            profileNumberGroupTextView.setText(numberOfUsers + " пользователя");
        }else{
            profileNumberGroupTextView.setText(numberOfUsers + " пользователей");
        }
    }






    private void groupUserManagement() {
        showGroupUsersListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View customListViewInALDialog = getLayoutInflater().inflate(R.layout.recycler_view_screen, null);
                builder.setView(customListViewInALDialog);
                AlertDialog alertDialog = builder.create();
                RecyclerView recyclerView = customListViewInALDialog.findViewById(R.id.RecyclerViewInCustomScreen);

                viewModel.getListGroupUsers();
                viewModel.onGotListGroupUsesLiveData.observe(getViewLifecycleOwner(), new Observer<ArrayList<groupUserData>>() {
                    @Override
                    public void onChanged(ArrayList<groupUserData> groupUserData) {
                        showListOfUsers(groupUserData, recyclerView);
                        viewModel.onGotListGroupUsesLiveData.removeObservers(getViewLifecycleOwner());
                    }
                });

                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        View customViewForAnim = alertDialog.getWindow().getDecorView();
                        int height = customViewForAnim.getHeight();
                        int width = customViewForAnim.getWidth();

                        Animator animation = ViewAnimationUtils.createCircularReveal(
                                customViewForAnim,
                                width / 2,
                                height / 2,
                                1F,
                                Math.max(width, height)
                        );

                        animation.setDuration(600);
                        animation.setInterpolator(new AccelerateDecelerateInterpolator());
                        animation.start();
                    }
                });

                alertDialog.show();
            }
        });
    }

    private void showListOfUsers(ArrayList<groupUserData> listUsers, RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        AdapterListUsers = new AdapterListUsers(listUsers, new adapterOnClickInterface() {
            @Override
            public void callback(Object data) {
                deleteGroupUser((groupUserData) data);
            }
        });
        recyclerView.setAdapter(AdapterListUsers);

    }


    private void deleteGroupUser(groupUserData deleteUser){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.alert_dialog_exit, null);

        TextView textView = view.findViewById(R.id.textCustomLogOutAlertDialog);
        Button submit = view.findViewById(R.id.buttonCustomLogOutAlertDialogConfirm);
        Button cancel = view.findViewById(R.id.buttonCustomLogOutAlertDialogCancel);

        if(TextUtils.equals(User.getUID(), deleteUser.UserUID)){
            textView.setText("Вы не можете выгнать себя");
            submit.setText("Выгнать");
            submit.setVisibility(View.GONE);
        }else{
            textView.setText("Вы уверены что хотите выгнать " + deleteUser.userName + "?");
            submit.setText("Выгнать");
        }
        cancel.setText("Отмена");
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.kickGroupUser(deleteUser.UserUID);
                viewModel.onUserDeletedLiveData.observe(getViewLifecycleOwner(), new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        viewModel.onGotListGroupUsesLiveData.removeObservers(getViewLifecycleOwner());
                        AdapterListUsers.deleteGroupUserFromList(deleteUser);
                        alertDialog.cancel();
                        getProfileGroupData();
                    }
                });
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.cancel();
                }
        });
    }



    private void joinGroupAlertDialogListener() {
        userOptionGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View customView = getLayoutInflater().inflate(R.layout.alert_dialog_join_group, null);
                builder.setView(customView);

                AlertDialog alertDialog = builder.create();

                Button dialogCancel = customView.findViewById(R.id.btnCustomJoinGroupAlertDialogCancel);
                Button dialogSubmit = customView.findViewById(R.id.btnCustomJoinGroupAlertDialogSubmit);

                EditText editText = customView.findViewById(R.id.editCustomJoinGroupAlertDialogField);

                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        showCircleAnim(alertDialog.getWindow().getDecorView());
                    }
                });

                alertDialog.show();

                dialogSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogSubmit.setClickable(false);
                        String groupKey = editText.getText().toString();
                        if (!TextUtils.isEmpty(groupKey)) {
                            viewModel.joinGroup(groupKey);
                            viewModel.userJoinGroup.observe(getViewLifecycleOwner(), new Observer<String>() {
                                @Override
                                public void onChanged(String s) {
                                    dialogSubmit.setClickable(true);
                                    if(TextUtils.equals(s, "user joined group")){
                                        alertDialog.cancel();
                                        showProfileUserData();
                                    }else if (TextUtils.equals(s, "No such group")){
                                        editText.setError("Такой группы не существует");
                                    }
                                    viewModel.userJoinGroup.removeObservers(getViewLifecycleOwner());
                                }
                            });
                        } else {
                            editText.setError("Введите код группы");
                        }
                    }
                });
                dialogCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });

            }
        });
    }

    private void exitGroupAlertDialogListener() {
        userOptionGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View customView = getLayoutInflater().inflate(R.layout.alert_dialog_exit, null);
                builder.setView(customView);

                AlertDialog alertDialog = builder.create();

                Button dialogCancel = customView.findViewById(R.id.buttonCustomLogOutAlertDialogCancel);
                Button dialogConfirm = customView.findViewById(R.id.buttonCustomLogOutAlertDialogConfirm);

                dialogCancel.setText("Отмена");
                dialogConfirm.setText("Выйти");

                ((TextView) customView.findViewById(R.id.textCustomLogOutAlertDialog)).setText("Вы уверены, что хотите покинуть группу?");

                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        showCircleAnim(alertDialog.getWindow().getDecorView());
                    }
                });
                alertDialog.show();
                dialogConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogConfirm.setClickable(false);
                        viewModel.exitGroup();
                        viewModel.userExitGroup.observe(getViewLifecycleOwner(), new Observer<String>() {
                            @Override
                            public void onChanged(String s) {
                                dialogConfirm.setClickable(true);
                                Log.d("Aboba", s);
                                if(TextUtils.equals(s, "user exit group")){
                                    alertDialog.cancel();
                                    showProfileUserData();
                                }
                                viewModel.userJoinGroup.removeObservers(getViewLifecycleOwner());
                            }
                        });
                    }
                });
                dialogCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });
            }
        });
    }
    private void logOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View customView = getLayoutInflater().inflate(R.layout.alert_dialog_exit, null);
        builder.setView(customView);

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                showCircleAnim(alertDialog.getWindow().getDecorView());
            }
        });
        alertDialog.show();

        Button dialogCancel = customView.findViewById(R.id.buttonCustomLogOutAlertDialogCancel);
        Button dialogConfirm = customView.findViewById(R.id.buttonCustomLogOutAlertDialogConfirm);

        dialogCancel.setText("Отмена");
        dialogConfirm.setText("Выйти");
        ((TextView) customView.findViewById(R.id.textCustomLogOutAlertDialog)).setText("Вы уверены, что хотите выйти из аккаунта?");

        dialogConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.exitAcc();
                alertDialog.cancel();
                startActivity(new Intent(getActivity(), Login.class));
                requireActivity().finish();
            }
        });
        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
    }

    private void showCircleAnim(View view){
        int height = view.getHeight();
        int width = view.getWidth();

        Animator animation = ViewAnimationUtils.createCircularReveal(
                view,
                width / 2,
                height / 2,
                1F,
                Math.max(width, height)
        );

        animation.setDuration(500);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.start();
    }

}