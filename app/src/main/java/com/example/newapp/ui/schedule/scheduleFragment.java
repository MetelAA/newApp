package com.example.newapp.ui.schedule;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


import com.example.newapp.R;
import com.example.newapp.adapters.ViewPagerScheduleAdapter;

import com.example.newapp.databinding.FragmentScheduleBinding;
import com.example.newapp.domain.models.arrayListForSchedule;
import com.example.newapp.interfaces.adapterOnBindViewHolder;
import com.example.newapp.ui.schedule.supportScreens.changeLessonsForSchedule;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class scheduleFragment extends Fragment {

    private FragmentScheduleBinding binding;

    private scheduleViewModelImpl viewModel;

    private ViewGroup mainElem;

    private ImageButton settingsBtnSchedule;
    private ImageButton changeBtnSchedule;

    private TextView textDaySchedule;
    private TextView textDateSchedule;

    private ViewPager2 viewPager;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentScheduleBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(this).get(scheduleViewModelImpl.class);

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
                startActivity(new Intent(getActivity(), changeLessonsForSchedule.class));
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
        setObservers();
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
        ViewPagerScheduleAdapter adapter = new ViewPagerScheduleAdapter(daysOfWeek, new adapterOnBindViewHolder() {
            @Override
            public void callback(String param) {
                Log.d("aboba", "вызов запроса через вью моедль по дню " + param);
                viewModel.getDayLessons(param);
            }
        });

        viewModel.onGotDayLessonsLiveData.observe(getViewLifecycleOwner(), new Observer<arrayListForSchedule>() {
            @Override
            public void onChanged(arrayListForSchedule lessonsList) {
                Log.d("Aboba", "on live data changed");
                adapter.addNewDayLessons(lessonsList);
            }
        });

        final int[] previousPosition = {defaultDayOfWeek};

        int finalDefaultDayOfWeek = defaultDayOfWeek;
        viewPager.post(new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(finalDefaultDayOfWeek);
            }
        });

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
    }

    private void setObservers(){
        viewModel.onErrorLiveData.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Snackbar.make(mainElem, s, Snackbar.ANIMATION_MODE_SLIDE).show();
            }
        });
    }

}