package com.example.newapp.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


import com.example.newapp.R;
import com.example.newapp.adapters.ViewPagerScheduleAdapter;

import com.example.newapp.databinding.FragmentScheduleBinding;
import com.example.newapp.ui.screenForSchedule.groupSettingsActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class scheduleFragment extends Fragment {

    private FirebaseFirestore fStore;
    private FragmentScheduleBinding binding;

    private ViewGroup mainElem;

    private ImageButton settingsBtnSchedule;
    private ImageButton changeBtnSchedule;

    private TextView textDaySchedule;
    private TextView textDateSchedule;

    private ViewPager2 viewPager;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fStore = FirebaseFirestore.getInstance();

        binding = FragmentScheduleBinding.inflate(inflater, container, false);

        settingsBtnSchedule = binding.settingsBtnSchedule;
        changeBtnSchedule = binding.changeBtnSchedule;
        viewPager = binding.viewpagerForShowSchedule;
        textDateSchedule = binding.textDateSchedule;


        textDaySchedule = binding.textDaySchedule;

        View v = binding.getRoot();
        mainElem =  getActivity().findViewById(android.R.id.content);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        

        settingsBtnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), groupSettingsActivity.class));
            }
        });

        changeBtnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("dayOfWeek", textDaySchedule.getText().toString());
                Navigation.findNavController(view).navigate(R.id.action_scheduleFragment_to_changeScheduleScreenForScheduleFragment, bundle);
            }
        });

        showSchedule();
    }
    String[] daysOfWeek = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"};


    private void showSchedule(){

        Calendar calendar = Calendar.getInstance();
        int defaultDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 2;
        if(calendar.get(Calendar.DAY_OF_WEEK) == 1){
            defaultDayOfWeek = 0;
            calendar.add(Calendar.DATE, 1);
        }
        ViewPagerScheduleAdapter adapter = new ViewPagerScheduleAdapter(daysOfWeek, fStore);

        final int[] previousPosition = {defaultDayOfWeek};


        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                textDaySchedule.setText(daysOfWeek[position]);
                if(position > previousPosition[0]){
                    calendar.add(Calendar.DAY_OF_WEEK, 1);
                }else if(position < previousPosition[0]){
                    calendar.add(Calendar.DAY_OF_WEEK, -1);
                }
                previousPosition[0] = position;
                textDateSchedule.setText(new SimpleDateFormat("yyyy.MM.dd").format(calendar.getTime()));
            }
        });


        viewPager.setAdapter(adapter);

        int finalDefaultDayOfWeek = defaultDayOfWeek;
        viewPager.post(new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(finalDefaultDayOfWeek);
            }
        });

    }



}