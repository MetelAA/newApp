package com.example.newapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;


import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private ConstraintLayout mainElem;
    private FirebaseFirestore fStore;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        fStore = FirebaseFirestore.getInstance();
        mainElem = findViewById(R.id.mainElemMainAct);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_container);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                switch(navDestination.getLabel().toString()){
                    case "fragment_chat":
                        bottomNavigationView.setVisibility(View.GONE);
                        break;
                    case "fragment_create_new_chat":
                    case "fragment_change_schedule_screen_for_schedule":
                        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
                            bottomNavigationView.getMenu().getItem(i).setEnabled(false);
                        }
                        bottomNavigationView.animate()
                                        .translationY(bottomNavigationView.getHeight())
                                        .setDuration(300)
                                        .setInterpolator(new AccelerateDecelerateInterpolator())
                                        .setListener(new AnimatorListenerAdapter() {
                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            bottomNavigationView.setVisibility(View.GONE);
                                            }
                                        })
                                .start();
                    break;
                    default:
                        if (bottomNavigationView.getVisibility() == View.GONE){
                            bottomNavigationView.setClickable(true);
                            bottomNavigationView.setVisibility(View.VISIBLE);
                            bottomNavigationView.animate()
                                    .translationY(0)
                                    .translationX(0)
                                    .setDuration(300)
                                    .setInterpolator(new AccelerateDecelerateInterpolator())
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
                                                bottomNavigationView.getMenu().getItem(i).setEnabled(true);
                                            }
                                        }
                                    })
                                    .start();
                        }
                    break;
                }
            }
        });
    }



    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}