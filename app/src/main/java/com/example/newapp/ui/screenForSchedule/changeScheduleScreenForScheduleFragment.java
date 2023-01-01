package com.example.newapp.ui.screenForSchedule;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
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
import com.example.newapp.core.LessonForShowSchedule;
import com.example.newapp.core.LessonForScheduleSettings;
import com.example.newapp.core.db.addDayLessons;
import com.example.newapp.core.db.getDayLessons;
import com.example.newapp.core.db.getLessonDescription;
import com.example.newapp.databinding.FragmentChangeScheduleScreenForScheduleBinding;
import com.example.newapp.interfaces.CallbackInterface;
import com.example.newapp.interfaces.CallbackInterfaceWithList;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class changeScheduleScreenForScheduleFragment extends Fragment {

    private FirebaseFirestore fStore;

    private FragmentChangeScheduleScreenForScheduleBinding binding;

    private ViewGroup mainElem;

    private TextView textDay;

    private ImageButton changesHasDoneChangeScheduleBtm;
    private ImageButton addDayInLinearLayoutSchedule;
    private ImageButton undoChangesSchedule;

    private ScrollView scrollViewForChangeSchedule;
    private LinearLayout linearLayoutForChangeInScrollViewSchedule;

    private ArrayList<LessonForScheduleSettings> listLessonsDescriptionForSpinner;

    private String dayOfWeek;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChangeScheduleScreenForScheduleBinding.inflate(inflater, container, false);

        fStore = FirebaseFirestore.getInstance();


        textDay = binding.textDayChangeScheduleScreen;

        changesHasDoneChangeScheduleBtm = binding.changesHasDoneChangeScheduleBtm;
        addDayInLinearLayoutSchedule = binding.addDayInLinearLayoutSchedule;
        undoChangesSchedule = binding.undoChangesScheduleBtnSchedule;

        scrollViewForChangeSchedule = binding.scrollViewForChangeSchedule;
        linearLayoutForChangeInScrollViewSchedule = binding.linearLayoutForChangeInScrollViewSchedule;


        mainElem = binding.getRoot();

        dayOfWeek = getArguments().getString("dayOfWeek");

        textDay.setText(dayOfWeek);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        changeSchedule();
    }

    private void changeSchedule() {
        undoChangesSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToSchedule();
            }
        });

        localCallbackInterface callbackInterface = new localCallbackInterface() {
            @Override
            public void callback(String status) {
                switch (status) {
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
                        Snackbar.make(mainElem, "ошибка", Snackbar.LENGTH_LONG).show();
                        backToSchedule();
                        break;
                }
            }
        };
        getLessonsDescription(callbackInterface);
    }


    private void getLessonsDescription(localCallbackInterface callbackInterface) {
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

    private void checkDayLessonExist(localCallbackInterface callbackInterface) {
        getDayLessons getDayLessons = new getDayLessons(new CallbackInterfaceWithList() {
            @Override
            public void requestResult(ArrayList list) {
                if (!(list == null)) {
                    for (Object objLesson : list) {

                        LessonForShowSchedule lesson = (LessonForShowSchedule) objLesson;
                        View customView = getLayoutInflater().inflate(R.layout.layout_for_change_shcedule, null);
                        linearLayoutForChangeInScrollViewSchedule.addView(customView);
                        Spinner lessonConstructorSpinner = customView.findViewById(R.id.spinnerCustomLayoutChangeScheduleSchedule);
                        final SpinnerAdapterListLessonNames[] spinnerAdapter = new SpinnerAdapterListLessonNames[1];
                        ImageButton deleteConstructorBtn = customView.findViewById(R.id.deleteNewSubjectConstructorChangeScheduleSchedule);

                        ((EditText) customView.findViewById(R.id.lessonNumberEditTextChangeScheduleSchedule)).setText(lesson.numberLesson);
                        ((EditText) customView.findViewById(R.id.startTimeEditTextChangeScheduleSchedule)).setText(lesson.timeStart);
                        ((EditText) customView.findViewById(R.id.endTimeEditTextChangeScheduleSchedule)).setText(lesson.timeEnd);
                        ((EditText) customView.findViewById(R.id.cabinetNumberEditTextChangeScheduleSchedule)).setText(lesson.studyRoom);

                        spinnerAdapter[0] = new SpinnerAdapterListLessonNames(getContext(), listLessonsDescriptionForSpinner);
                        lessonConstructorSpinner.setAdapter(spinnerAdapter[0]);
                        LessonForScheduleSettings lessonForScheduleDesc = new LessonForScheduleSettings(lesson.subject, lesson.teacher);

                        for (int i = 0; i < listLessonsDescriptionForSpinner.size(); i++) {
                            if (TextUtils.equals(lessonForScheduleDesc.subjectName, listLessonsDescriptionForSpinner.get(i).subjectName) && TextUtils.equals(lessonForScheduleDesc.teacherName, listLessonsDescriptionForSpinner.get(i).teacherName)) {
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
                callbackInterface.callback("ok");
            }

            @Override
            public void throwError(String error) {
                Snackbar.make(mainElem, error, Snackbar.LENGTH_LONG).show();
                callbackInterface.callback("error");
            }
        });
        getDayLessons.getLessons(fStore, dayOfWeek);
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
            if (Integer.parseInt(lessonNumber.getText().toString()) > 10) {
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
                backToSchedule();
            }

            @Override
            public void throwError(String error) {
                Snackbar.make(mainElem, error, Snackbar.LENGTH_LONG).show();
                backToSchedule();
            }
        });
        addDayLessons.addLessons(fStore, dayLessonList, dayOfWeek);
    }

    private void showNewSubjectConstructor() {
        View customView = getLayoutInflater().inflate(R.layout.layout_for_change_shcedule, null);
        linearLayoutForChangeInScrollViewSchedule.addView(customView);
        Spinner lessonConstructorSpinner = customView.findViewById(R.id.spinnerCustomLayoutChangeScheduleSchedule);
        final SpinnerAdapterListLessonNames[] spinnerAdapter = new SpinnerAdapterListLessonNames[1];
        ImageButton deleteConstructorBtn = customView.findViewById(R.id.deleteNewSubjectConstructorChangeScheduleSchedule);

        spinnerAdapter[0] = new SpinnerAdapterListLessonNames(getContext(), listLessonsDescriptionForSpinner);
        lessonConstructorSpinner.setAdapter(spinnerAdapter[0]);

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


    private interface localCallbackInterface {
        void callback(String status);
    }
}