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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class scheduleFragment extends Fragment {

    private FragmentScheduleBinding binding;

    private scheduleViewModelImpl viewModel;

    private ViewGroup mainElem;

    private ImageButton settingsBtnSchedule;
    private ImageButton changeBtnSchedule;

    private TextView textDaySchedule;
    private TextView textDateSchedule;

    private LocalDate selectDate;

    private ViewPager2 viewPager;
    private ViewPagerScheduleAdapter viewPagerAdapter;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentScheduleBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(this).get(scheduleViewModelImpl.class);

        settingsBtnSchedule = binding.settingsBtnSchedule;
        changeBtnSchedule = binding.changeBtnSchedule;
        viewPager = binding.viewpagerForShowSchedule;
        textDateSchedule = binding.textDateSchedule;


        textDaySchedule = binding.textDaySchedule;
        selectDate = LocalDate.now();

        View v = binding.getRoot();
        mainElem =  getActivity().findViewById(android.R.id.content);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setListeners();
        setObservers();
        showSchedule();
    }

    private void showSchedule() {
        ViewPagerScheduleAdapter adapter = new ViewPagerScheduleAdapter(new adapterOnBindViewHolder() {
            @Override
            public void callback(String param) {
                //Log.d("aboba", "вызов запроса через вью моедль по дню " + param);
                viewModel.getDayLessons(param);
            }
        });

        viewModel.onGotDayLessonsLiveData.observe(getViewLifecycleOwner(), new Observer<arrayListForSchedule>() {
            @Override
            public void onChanged(arrayListForSchedule lessonsList) {
                Log.d("Aboba", lessonsList.toString() + " lessonsList.toString()");
                //Log.d("Aboba", "on live data changed");
                adapter.addNewDayLessons(lessonsList);
            }
        });


        viewPager.post(new Runnable() {
            @Override
            public void run() {
                Log.d("Aboba", selectDate.getDayOfWeek().getValue() + " ");
                viewPager.setCurrentItem(selectDate.getDayOfWeek().getValue() - 1);
                showTextSchedule();
                registerPageChangeCallback();
            }

            private void registerPageChangeCallback() {
                viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        selectDate = selectDate.plusDays(position - (selectDate.getDayOfWeek().getValue() - 1));
                        showTextSchedule();
                    }
                });
            }
        });


        viewPager.setAdapter(adapter);
    }

    private void showTextSchedule(){
        textDaySchedule.setText(formatDateToDayOfWeek(selectDate));
        textDateSchedule.setText(selectDate.getYear() + "." + selectDate.getMonthValue() + "." + selectDate.getDayOfMonth());
    }

    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("EEEE").withLocale( new Locale("ru"));
    private String formatDateToDayOfWeek(LocalDate localDate){
        String trueFormat = dateFormat.format(localDate);
        return trueFormat.substring(0,1).toUpperCase() + trueFormat.substring(1);
    }

    private void setObservers(){
        viewModel.onErrorLiveData.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Snackbar.make(mainElem, s, Snackbar.ANIMATION_MODE_SLIDE).show();
            }
        });
    }


    private void setListeners(){
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
                bundle.putSerializable("selectDate", selectDate);
                Navigation.findNavController(v).navigate(R.id.action_scheduleFragment_to_changeScheduleScreenForScheduleFragment, bundle);
            }
        });
    }
}