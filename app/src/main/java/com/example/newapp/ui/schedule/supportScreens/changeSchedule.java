package com.example.newapp.ui.schedule.supportScreens;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.newapp.R;
import com.example.newapp.adapters.SpinnerAdapterListLessonNames;
import com.example.newapp.domain.models.arrayListForSchedule;
import com.example.newapp.domain.models.lesson;
import com.example.newapp.domain.models.lessonDescription;
import com.example.newapp.domain.models.oldModels.LessonForScheduleSettings;
import com.example.newapp.databinding.FragmentChangeScheduleScreenForScheduleBinding;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;


public class changeSchedule extends Fragment {


    private FragmentChangeScheduleScreenForScheduleBinding binding;

    private ViewGroup mainElem;
    private changeScheduleViewModelImpl viewModel;

    private TextView textDay;

    private ImageButton changesHasDoneChangeScheduleBtm;
    private ImageButton addDayInLinearLayoutSchedule;
    private ImageButton undoChangesSchedule;

    private ScrollView scrollViewForChangeSchedule;
    private LinearLayout linearLayoutForChangeInScrollViewSchedule;

    private ArrayList<lessonDescription> listLessonsDescription;

    private LocalDate selectDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChangeScheduleScreenForScheduleBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(changeScheduleViewModelImpl.class);

        textDay = binding.textDayChangeScheduleScreen;

        changesHasDoneChangeScheduleBtm = binding.changesHasDoneChangeScheduleBtm;
        addDayInLinearLayoutSchedule = binding.addDayInLinearLayoutSchedule;
        undoChangesSchedule = binding.undoChangesScheduleBtnSchedule;

        scrollViewForChangeSchedule = binding.scrollViewForChangeSchedule;
        linearLayoutForChangeInScrollViewSchedule = binding.linearLayoutForChangeInScrollViewSchedule;


        mainElem = binding.mainElemChangeScheduleDesc;

        assert getArguments() != null;
        selectDate = (LocalDate) getArguments().getSerializable("selectDate");


        textDay.setText(formatDateToDayOfWeek(selectDate));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        undoChangesSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToSchedule();
            }
        });

        getLessonsDescription();
        setObservers();
    }


    private void getLessonsDescription() {
        viewModel.getLessonsDescription();
        viewModel.onGotLessonsDescription.observe(getViewLifecycleOwner(), new Observer<ArrayList<lessonDescription>>() {
            @Override
            public void onChanged(ArrayList<lessonDescription> lessonDescriptions) {
                listLessonsDescription = lessonDescriptions;
                checkDayLessonExist();

                addDayInLinearLayoutSchedule.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showNewSubjectConstructor();
                    }
                });

                changesHasDoneChangeScheduleBtm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        acceptChangesSchedule();
                    }
                });
            }
        });
    }

    private void checkDayLessonExist() {
        viewModel.getDayLessons(selectDate.getDayOfWeek().toString());

        viewModel.onGotDayLessonsLiveData.observe(getViewLifecycleOwner(), new Observer<arrayListForSchedule>() {
            @Override
            public void onChanged(arrayListForSchedule arrayListForSchedule) {
                for (Object objLesson : arrayListForSchedule) {
                    lesson lesson = (lesson) objLesson;
                    View customView = getLayoutInflater().inflate(R.layout.layout_for_change_shcedule, null);
                    linearLayoutForChangeInScrollViewSchedule.addView(customView);
                    Spinner lessonConstructorSpinner = customView.findViewById(R.id.spinnerCustomLayoutChangeScheduleSchedule);
                    ImageButton deleteConstructorBtn = customView.findViewById(R.id.deleteNewSubjectConstructorChangeScheduleSchedule);

                    ((EditText) customView.findViewById(R.id.lessonNumberEditTextChangeScheduleSchedule)).setText(lesson.numberLesson);
                    ((EditText) customView.findViewById(R.id.startTimeEditTextChangeScheduleSchedule)).setText(lesson.timeStart);
                    ((EditText) customView.findViewById(R.id.endTimeEditTextChangeScheduleSchedule)).setText(lesson.timeEnd);
                    ((EditText) customView.findViewById(R.id.cabinetNumberEditTextChangeScheduleSchedule)).setText(lesson.studyRoom);

                    SpinnerAdapterListLessonNames spinnerAdapter = new SpinnerAdapterListLessonNames(getContext(), listLessonsDescription);
                    lessonConstructorSpinner.setAdapter(spinnerAdapter);
                    LessonForScheduleSettings lessonForScheduleDesc = new LessonForScheduleSettings(lesson.subject, lesson.teacher);

                    for (int i = 0; i < listLessonsDescription.size(); i++) {
                        if (TextUtils.equals(lessonForScheduleDesc.subjectName, listLessonsDescription.get(i).subjectName) && TextUtils.equals(lessonForScheduleDesc.teacherName, listLessonsDescription.get(i).teacherName)) {
                            lessonConstructorSpinner.setSelection(i);
                        }
                    }

                    deleteConstructorBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            linearLayoutForChangeInScrollViewSchedule.removeView(customView);
                        }
                    });
                }
            }
        });



    }


    private void acceptChangesSchedule() {
        ArrayList<lesson> listLessons = new ArrayList<>();
        for (int i = 0; i < linearLayoutForChangeInScrollViewSchedule.getChildCount(); i++) {
            View v = linearLayoutForChangeInScrollViewSchedule.getChildAt(i);
            EditText lessonNumberEditText = v.findViewById(R.id.lessonNumberEditTextChangeScheduleSchedule);
            EditText startTimeEditText = v.findViewById(R.id.startTimeEditTextChangeScheduleSchedule);
            EditText endTimeEditText = v.findViewById(R.id.endTimeEditTextChangeScheduleSchedule);
            EditText studyRoomEditText = v.findViewById(R.id.cabinetNumberEditTextChangeScheduleSchedule);
            Spinner spinnerEditText = v.findViewById(R.id.spinnerCustomLayoutChangeScheduleSchedule);

            String lessonNumber = lessonNumberEditText.getText().toString().trim();
            String startTime = startTimeEditText.getText().toString().trim();
            String endTime = endTimeEditText.getText().toString().trim();
            String studyRoom = studyRoomEditText.getText().toString().trim();

            if (TextUtils.isEmpty(lessonNumber)) {
                lessonNumberEditText.setError("Ввидте номер урока");
                return;
            }
            if (Integer.parseInt(lessonNumber) > 10) {
                lessonNumberEditText.setError("Количество уроков не может превыщать 10");
                return;
            }
            if (TextUtils.isEmpty(startTime)) {
                startTimeEditText.setError("Ввидте время начало урока");
                return;
            }
            if (TextUtils.isEmpty(endTime)) {
                endTimeEditText.setError("Ввидте время конца урока");
                return;
            }
            if (TextUtils.isEmpty(studyRoom)) {
                studyRoomEditText.setError("Ввидте номер кабинета");
                return;
            }
            if(spinnerEditText.getSelectedItem() == null){
                lessonNumberEditText.setError("Выберете урок в выпадающем меню");
                return;
            }

            lesson lesson = new lesson(
                    lessonNumber,
                    startTime,
                    endTime,
                    studyRoom,
                    ((lessonDescription) spinnerEditText.getSelectedItem()).teacherName,
                    ((lessonDescription) spinnerEditText.getSelectedItem()).subjectName
            );
            listLessons.add(lesson);
            Log.d("Aboba", "added lesson " + lesson.toString());
        }
        viewModel.addDayLessons(listLessons, selectDate.getDayOfWeek().toString());
    }

    private void showNewSubjectConstructor() { //проверить зачем здесь одноэлементный массив со спинером
        View customView = getLayoutInflater().inflate(R.layout.layout_for_change_shcedule, null);
        linearLayoutForChangeInScrollViewSchedule.addView(customView);
        Spinner lessonConstructorSpinner = customView.findViewById(R.id.spinnerCustomLayoutChangeScheduleSchedule);
        ImageButton deleteConstructorBtn = customView.findViewById(R.id.deleteNewSubjectConstructorChangeScheduleSchedule);

        SpinnerAdapterListLessonNames spinnerAdapter = new SpinnerAdapterListLessonNames(getContext(), listLessonsDescription);
        lessonConstructorSpinner.setAdapter(spinnerAdapter);

        deleteConstructorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayoutForChangeInScrollViewSchedule.removeView(customView);
            }
        });


    }

    private void backToSchedule(){
        Navigation.findNavController(mainElem).navigate(R.id.action_changeScheduleScreenForScheduleFragment_to_scheduleFragment2);
    }
    private void setObservers(){
        viewModel.onErrorLiveData.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Snackbar.make(mainElem, s, Snackbar.ANIMATION_MODE_SLIDE).show();
            }
        });

        viewModel.onDayLessonsAddedLiveData.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(TextUtils.equals(s, "ok")){
                    backToSchedule();
                }
            }
        });
    }


    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("EEEE").withLocale( new Locale("ru"));
    private String formatDateToDayOfWeek(LocalDate localDate){
        String trueFormat = dateFormat.format(localDate);
        return trueFormat.substring(0,1).toUpperCase() + trueFormat.substring(1);
    }
}