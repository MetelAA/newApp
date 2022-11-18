package com.example.newapp.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.example.newapp.R;
import com.example.newapp.core.ChangeSchedule;
import com.example.newapp.core.Lesson;
import com.example.newapp.core.User;
import com.example.newapp.core.showSchedule;
import com.example.newapp.databinding.FragmentScheduleBinding;
import com.example.newapp.interfaces.CallbackInterface;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class scheduleFragment extends Fragment {


    private FragmentScheduleBinding binding;

    private int selector = 1;

    private ListView scheduleListViewSchedule;
    private ConstraintLayout changeScheduleConstraintLayoutSchedule;

    private ImageButton btnShowNextSchedule;
    private ImageButton btnShowPreviousSchedule;
    private TextView dayTextSchedule;
    private ImageButton changeBtnSchedule;
    private ImageButton doneBtnSchedule;
    private ViewGroup mainElem;

    private FirebaseFirestore fStore;

    private String dayOfWeek;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentScheduleBinding.inflate(inflater, container, false);

        btnShowNextSchedule = binding.btnShowNextSchedule;
        btnShowPreviousSchedule = binding.btnShowPreviousSchedule;
        dayTextSchedule = binding.dayTextSchedule;
        changeBtnSchedule = binding.changeBtnSchedule;
        doneBtnSchedule = binding.doneBtnSchedule;
        scheduleListViewSchedule = binding.scheduleListViewSchedule;
        changeScheduleConstraintLayoutSchedule = binding.changeScheduleConstraintLayoutSchedule;

        mainElem = binding.getRoot();

        fStore = FirebaseFirestore.getInstance();


        View v = binding.getRoot();
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnShowNextSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selector++;
                selectDay();
            }
        });

        btnShowPreviousSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selector--;
                selectDay();
            }
        });


        showInterface();
    }


    private void changeBtnScheduleListener() {

        changeBtnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleListViewSchedule.setVisibility(View.GONE);
                changeBtnSchedule.setVisibility(View.GONE);
                doneBtnSchedule.setVisibility(View.VISIBLE);
                changeScheduleConstraintLayoutSchedule.setVisibility(View.VISIBLE);
                btnShowNextSchedule.setClickable(false);
                btnShowPreviousSchedule.setClickable(false);
                binding.addLessonSchedule.setVisibility(View.VISIBLE);

                final int[] position = {1};
                LinearLayout linearLayout = binding.changeListLessonsSchedule;
                List<View> ListOfViews = new ArrayList<>();
                List<Lesson> ListOfLessons = new ArrayList<>();

                binding.addLessonSchedule.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View view = getLayoutInflater().inflate(R.layout.custom_view_item_for_change_schedule, null);
                        ((TextView) view.findViewById(R.id.lessonNumberChangeSchedule)).setText(String.valueOf(position[0]) + " Урок");
                        ListOfViews.add(view);
                        linearLayout.addView(view);
                        position[0]++;
                    }
                });

                doneBtnSchedule.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int acc = 1;
                        for (View view : ListOfViews) {
                            String lessonName = ((EditText) view.findViewById(R.id.lessonNameChangeSchedule)).getText().toString();
                            String lessonTime = ((EditText) view.findViewById(R.id.lessonTimeChangeSchedule)).getText().toString();
                            if (TextUtils.isEmpty(lessonName)) {
                                ((EditText) view.findViewById(R.id.lessonNameChangeSchedule)).setError("Введите название");
                                return;
                            }

                            if (TextUtils.isEmpty(lessonTime)) {
                                ((EditText) view.findViewById(R.id.lessonTimeChangeSchedule)).setError("Введите время");
                                return;
                            }
                            ListOfLessons.add(new Lesson(lessonName, lessonTime, acc));
                            acc++;
                        }
                        ChangeSchedule changeSchedule = new ChangeSchedule(new CallbackInterface() {
                            @Override
                            public void callback(String status) {
                                linearLayout.removeAllViews();
                                showScheduleMet();
                            }
                        });
                        changeSchedule.changeSchedule(mainElem, fStore, ListOfLessons, dayOfWeek);

                    }
                });

            }
        });
    }

    private void showScheduleMet() {
        scheduleListViewSchedule.setVisibility(View.VISIBLE);
        changeBtnSchedule.setVisibility(View.VISIBLE);
        doneBtnSchedule.setVisibility(View.GONE);
        changeScheduleConstraintLayoutSchedule.setVisibility(View.GONE);
        btnShowNextSchedule.setClickable(true);
        btnShowPreviousSchedule.setClickable(true);
        binding.addLessonSchedule.setVisibility(View.GONE);
        showSchedule showSchedule = new showSchedule();
        showSchedule.showSchedule(mainElem, fStore, scheduleListViewSchedule, getContext(), dayOfWeek);
    }

    private void selectDay() {
        if (selector == 0) {
            selector = 6;
        }
        if (selector == 7) {
            selector = 1;
        }

        switch (selector) {
            case 1:
                dayTextSchedule.setText("Понедельник");
                dayOfWeek = "monday";
                showScheduleMet();
                break;
            case 2:
                dayTextSchedule.setText("Вторник");
                dayOfWeek = "tuesday";
                showScheduleMet();
                break;
            case 3:
                dayTextSchedule.setText("Среда");
                dayOfWeek = "wednesday";
                showScheduleMet();
                break;
            case 4:
                dayTextSchedule.setText("Четверг");
                dayOfWeek = "thursday";
                showScheduleMet();
                break;
            case 5:
                dayTextSchedule.setText("Пятница");
                dayOfWeek = "friday";
                showScheduleMet();
                break;
            case 6:
                dayTextSchedule.setText("Суббота");
                dayOfWeek = "saturday";
                showScheduleMet();
                break;
        }
    }

    private void showInterface() {
        if (TextUtils.equals(User.getType(), "Учитель")) {
            changeBtnSchedule.setVisibility(View.VISIBLE);
            changeBtnScheduleListener();
        } else {
            changeBtnSchedule.setVisibility(View.INVISIBLE);
        }
        selectDay();
    }


}