package com.example.newapp;

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
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;


import com.example.newapp.core.db.setUserStatus;
import com.example.newapp.interfaces.CallbackInterface;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    ConstraintLayout mainElem;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fStore = FirebaseFirestore.getInstance();
        mainElem = findViewById(R.id.mainElemMainAct);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_container);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);


        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                switch(navDestination.getLabel().toString()){
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
        setUserStatus setUserStatus = new setUserStatus(new CallbackInterface() {
            @Override
            public void requestStatus(String status) {

            }

            @Override
            public void throwError(String error) {
                Snackbar.make(mainElem, error, Snackbar.LENGTH_LONG).show();
            }
        });
        setUserStatus.setStatus(fStore, "offline");
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUserStatus setUserStatus = new setUserStatus(new CallbackInterface() {
            @Override
            public void requestStatus(String status) {

            }
            @Override
            public void throwError(String error) {
                Snackbar.make(mainElem, error, Snackbar.LENGTH_LONG).show();
            }
        });
        setUserStatus.setStatus(fStore, "online");
    }
}