package com.example.newapp.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;


import com.example.newapp.R;
import com.example.newapp.adapters.customAdapterListLessonNamesForSpinner;
import com.example.newapp.core.LessonForSchedule;
import com.example.newapp.core.LessonForScheduleSettings;
import com.example.newapp.core.db.addDayLessons;
import com.example.newapp.core.db.getDayLessons;
import com.example.newapp.core.db.getLessonDescription;
import com.example.newapp.databinding.FragmentScheduleBinding;
import com.example.newapp.interfaces.CallbackInterface;
import com.example.newapp.interfaces.CallbackInterfaceWithList;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class scheduleFragment extends Fragment {

    private FirebaseFirestore fStore;
    private FragmentScheduleBinding binding;

    private ViewGroup mainElem;

    private ImageButton settingsBtnSchedule;
    private ImageButton changeBtnSchedule;
    private ImageButton showPreviousDayBtnSchedule;
    private ImageButton showNextDayBtnSchedule;
    private ImageButton changesHasDoneChangeScheduleBtm;
    private ImageButton addDayInLinearLayoutSchedule;
    private  ImageButton undoChangesSchedule;

    private TextView textDaySchedule;

    private ScrollView scrollViewForChangeSchedule;
    private LinearLayout linearLayoutForChangeInScrollViewSchedule;





    private ArrayList<LessonForScheduleSettings> listLessonsDescriptionForSpinner;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fStore = FirebaseFirestore.getInstance();

        binding = FragmentScheduleBinding.inflate(inflater, container, false);

        settingsBtnSchedule = binding.settingsBtnSchedule;
        changeBtnSchedule = binding.changeBtnSchedule;
        showPreviousDayBtnSchedule = binding.showPreviousDayBtnSchedule;
        showNextDayBtnSchedule = binding.showNextDayBtnSchedule;
        changesHasDoneChangeScheduleBtm = binding.changesHasDoneChangeScheduleBtm;
        addDayInLinearLayoutSchedule = binding.addDayInLinearLayoutSchedule;
        undoChangesSchedule = binding.undoChangesScheduleBtnSchedule;
        scrollViewForChangeSchedule = binding.scrollViewForChangeSchedule;
        linearLayoutForChangeInScrollViewSchedule = binding.linearLayoutForChangeInScrollViewSchedule;

        textDaySchedule = binding.textDaySchedule;

        View v = binding.getRoot();
        mainElem = binding.getRoot();
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
                changeSchedule();
            }
        });


    }

    private void changeSchedule() {
        changeScheduleElementsActive();

        undoChangesSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeScheduleElementsInactive();
            }
        });

        localCallbackInterface callbackInterface = new localCallbackInterface() {
            @Override
            public void callback(String status) {
                switch (status){
                    case "ok":
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
                    break;
                    case "error":
                        changeScheduleElementsInactive();
                    break;
                }
            }
        };
        getLessonsDescription(callbackInterface);
    }



    private void getLessonsDescription(localCallbackInterface callbackInterface){
        getLessonDescription getLessonDescription = new getLessonDescription(new CallbackInterfaceWithList() {
            @Override
            public void requestResult(ArrayList list) {
                listLessonsDescriptionForSpinner = list;
                checkDayLessonExist(callbackInterface);
            }

            @Override
            public void throwError(String error) {
                Snackbar.make(mainElem, error, Snackbar.LENGTH_LONG).show();
                callbackInterface.callback("error");
            }
        });
        getLessonDescription.getLessonsFromDB(fStore);
    }

    private void checkDayLessonExist(localCallbackInterface callbackInterface){
        getDayLessons getDayLessons = new getDayLessons(new CallbackInterfaceWithList() {
            @Override
            public void requestResult(ArrayList list) {
                if(!(list == null)){
                    for (Object objLesson:list) {

                        LessonForSchedule lesson = (LessonForSchedule) objLesson;
                        View customView = getLayoutInflater().inflate(R.layout.layout_for_change_shcedule, null);
                        linearLayoutForChangeInScrollViewSchedule.addView(customView);
                        Spinner lessonConstructorSpinner = customView.findViewById(R.id.spinnerCustomLayoutChangeScheduleSchedule);
                        final customAdapterListLessonNamesForSpinner[] spinnerAdapter = new customAdapterListLessonNamesForSpinner[1];
                        ImageButton deleteConstructorBtn = customView.findViewById(R.id.deleteNewSubjectConstructorChangeScheduleSchedule);

                        ((EditText) customView.findViewById(R.id.lessonNumberEditTextChangeScheduleSchedule)).setText(lesson.numberLesson);
                        ((EditText) customView.findViewById(R.id.startTimeEditTextChangeScheduleSchedule)).setText(lesson.timeStart);
                        ((EditText) customView.findViewById(R.id.endTimeEditTextChangeScheduleSchedule)).setText(lesson.timeEnd);
                        ((EditText) customView.findViewById(R.id.cabinetNumberEditTextChangeScheduleSchedule)).setText(lesson.studyRoom);

                        spinnerAdapter[0] = new customAdapterListLessonNamesForSpinner(getContext(), listLessonsDescriptionForSpinner);
                        lessonConstructorSpinner.setAdapter(spinnerAdapter[0]);
                        LessonForScheduleSettings lessonForScheduleDesc = new LessonForScheduleSettings(lesson.subject, lesson.teacher);

//                        for (int i = 0; i < listLessonsDescriptionForSpinner.size(); i++) {
//                            if(TextUtils.equals(lessonForScheduleDesc.subjectName, listLessonsDescriptionForSpinner.get(i).subjectName)
//                                    &&
//                                    TextUtils.equals(lessonForScheduleDesc.teacherName, listLessonsDescriptionForSpinner.get(i).teacherName)
//                            ){
//                                Log.d("Aboba", "same " + String.valueOf(i));
//                            }
//                        }




                        lessonConstructorSpinner.setSelection(listLessonsDescriptionForSpinner.indexOf(lessonForScheduleDesc));


                        deleteConstructorBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                linearLayoutForChangeInScrollViewSchedule.removeView(customView);
                            }
                        });
                    }
                }



                callbackInterface.callback("ok");
            }
            @Override
            public void throwError(String error) {
                Snackbar.make(mainElem, error, Snackbar.LENGTH_LONG).show();
                callbackInterface.callback("error");
            }
        });
        getDayLessons.getLessons(fStore, textDaySchedule.getText().toString());
    }


    private void acceptChangesSchedule() {

        List<Map<String, String>> dayLessonList = new ArrayList<>();
        for (int i = 0; i < linearLayoutForChangeInScrollViewSchedule.getChildCount(); i++) {
            View v = linearLayoutForChangeInScrollViewSchedule.getChildAt(i);
            Map<String, String> lesson = new HashMap<>();

            EditText lessonNumber = v.findViewById(R.id.lessonNumberEditTextChangeScheduleSchedule);
            EditText startTime = v.findViewById(R.id.startTimeEditTextChangeScheduleSchedule);
            EditText endTime = v.findViewById(R.id.endTimeEditTextChangeScheduleSchedule);
            EditText cabinetNumber = v.findViewById(R.id.cabinetNumberEditTextChangeScheduleSchedule);
            Spinner spinner = v.findViewById(R.id.spinnerCustomLayoutChangeScheduleSchedule);

            if (TextUtils.isEmpty(lessonNumber.getText())) {
                lessonNumber.setError("Ввидте номер урока");
                return;
            }
            if(Integer.parseInt(lessonNumber.getText().toString()) > 10){
                lessonNumber.setError("Количество уроков не может превыщать 10");
                return;
            }
            if (TextUtils.isEmpty(startTime.getText())) {
                startTime.setError("Ввидте время начало урока");
                return;
            }
            if (TextUtils.isEmpty(endTime.getText())) {
                endTime.setError("Ввидте время конца урока");
                return;
            }
            if (TextUtils.isEmpty(cabinetNumber.getText())) {
                cabinetNumber.setError("Ввидте номер кабинета");
                return;
            }
            lesson.put("numberLesson", lessonNumber.getText().toString());
            lesson.put("timeStart", startTime.getText().toString());
            lesson.put("timeEnd", endTime.getText().toString());
            lesson.put("studyRoom", cabinetNumber.getText().toString());
            lesson.put("teacher", ((LessonForScheduleSettings) spinner.getSelectedItem()).teacherName);
            lesson.put("subject", ((LessonForScheduleSettings) spinner.getSelectedItem()).subjectName);

            dayLessonList.add(lesson);
        }
        addDayLessons addDayLessons = new addDayLessons(new CallbackInterface() {
            @Override
            public void requestStatus(String status) {
                changeScheduleElementsInactive();
            }

            @Override
            public void throwError(String error) {
                Snackbar.make(mainElem, error, Snackbar.LENGTH_LONG).show();
                changeScheduleElementsInactive();
            }
        });
        addDayLessons.addLessons(fStore, dayLessonList, textDaySchedule.getText().toString());
    }

    public void showNewSubjectConstructor() {
        View customView = getLayoutInflater().inflate(R.layout.layout_for_change_shcedule, null);
        linearLayoutForChangeInScrollViewSchedule.addView(customView);
        Spinner lessonConstructorSpinner = customView.findViewById(R.id.spinnerCustomLayoutChangeScheduleSchedule);
        final customAdapterListLessonNamesForSpinner[] spinnerAdapter = new customAdapterListLessonNamesForSpinner[1];
        ImageButton deleteConstructorBtn = customView.findViewById(R.id.deleteNewSubjectConstructorChangeScheduleSchedule);

        spinnerAdapter[0] = new customAdapterListLessonNamesForSpinner(getContext(), listLessonsDescriptionForSpinner);
        lessonConstructorSpinner.setAdapter(spinnerAdapter[0]);

        deleteConstructorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayoutForChangeInScrollViewSchedule.removeView(customView);
            }
        });


    }


    private void changeScheduleElementsActive() {
        showPreviousDayBtnSchedule.setClickable(false);
        showNextDayBtnSchedule.setClickable(false);
        settingsBtnSchedule.setVisibility(View.GONE);
        changeBtnSchedule.setVisibility(View.GONE);

        undoChangesSchedule.setVisibility(View.VISIBLE);
        changesHasDoneChangeScheduleBtm.setVisibility(View.VISIBLE);
        addDayInLinearLayoutSchedule.setVisibility(View.VISIBLE);
        scrollViewForChangeSchedule.setVisibility(View.VISIBLE);
    }

    private void changeScheduleElementsInactive() {
        showPreviousDayBtnSchedule.setClickable(true);
        showNextDayBtnSchedule.setClickable(true);
        settingsBtnSchedule.setVisibility(View.VISIBLE);
        changeBtnSchedule.setVisibility(View.VISIBLE);

        undoChangesSchedule.setVisibility(View.GONE);
        changesHasDoneChangeScheduleBtm.setVisibility(View.GONE);
        addDayInLinearLayoutSchedule.setVisibility(View.GONE);
        scrollViewForChangeSchedule.setVisibility(View.GONE);


        linearLayoutForChangeInScrollViewSchedule.removeAllViews();
    }

    private interface localCallbackInterface{
        void callback(String status);
    }
}