package com.example.newapp.ui.schedule.supportScreens;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.animation.Animator;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.newapp.R;
import com.example.newapp.adapters.AdapterListLessonsDescription;
import com.example.newapp.domain.models.lessonDescription;
import com.example.newapp.databinding.ActivityGroupSettingsBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class changeLessonsForSchedule extends AppCompatActivity {

    ActivityGroupSettingsBinding binding;
    ViewGroup mainElem;
    changeLessonsForScheduleViewModelImpl viewModel;

    ImageButton doneLessonDescriptionSettingsSchedule;
    Button addLessonDescriptionScheduleSettings;
    LinearLayout lessonDescriptionContainerScheduleSettings;
    EditText lessonNameEditTextScheduleSettings;
    EditText teacherNameScheduleSettings;
    ListView ListViewForListOfLessonsDescriptionScheduleSettings;


    AdapterListLessonsDescription adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupSettingsBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(changeLessonsForScheduleViewModelImpl.class);
        mainElem = binding.mainElemGroupSettings;


        doneLessonDescriptionSettingsSchedule = binding.doneLessonDescriptionSettingsSchedule;
        addLessonDescriptionScheduleSettings = binding.addLessonDescriptionScheduleSettings;
        lessonDescriptionContainerScheduleSettings = binding.lessonDescriptionContainerScheduleSettings;
        lessonNameEditTextScheduleSettings = binding.lessonNameEditTextScheduleSettings;
        teacherNameScheduleSettings = binding.teacherNameScheduleSettings;
        ListViewForListOfLessonsDescriptionScheduleSettings = binding.ListViewForListOfLessonsDescriptionScheduleSettings;



        binding.backToScheduleBtnScheduleSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setObservers();
        addNewLessonsListener();
        getLessons();
        setContentView(mainElem);
    }

    private void setObservers(){
        viewModel.onErrorLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Snackbar.make(mainElem, s, Snackbar.ANIMATION_MODE_SLIDE).show();
            }
        });

        viewModel.gotLessonsDescriptionLiveData.observe(this, new Observer<ArrayList<lessonDescription>>() {
            @Override
            public void onChanged(ArrayList<lessonDescription> lessonDescriptions) {
                showLessonsInListView(lessonDescriptions);
            }
        });

        viewModel.addedLessonDescriptionListLiveData.observe(this, new Observer<lessonDescription>() {
            @Override
            public void onChanged(lessonDescription lessonDescription) {
                adapter.add(lessonDescription);
                adapter.notifyDataSetChanged();
            }
        });

        viewModel.deletedLessonDescriptionLiveData.observe(this, new Observer<lessonDescription>() {
            @Override
            public void onChanged(lessonDescription lessonDescription) {
                adapter.remove(lessonDescription);
                adapter.notifyDataSetChanged();
            }
        });

    }


    private void addNewLessonsListener(){
        addLessonDescriptionScheduleSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLessonDescriptionScheduleSettings.setVisibility(View.GONE);
                lessonDescriptionContainerScheduleSettings.setVisibility(View.VISIBLE);
                doneLessonDescriptionSettingsSchedule.setVisibility(View.VISIBLE);

                doneLessonDescriptionSettingsSchedule.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String lessonName = lessonNameEditTextScheduleSettings.getText().toString();
                        String teacherName = teacherNameScheduleSettings.getText().toString();

                        if(lessonName.isEmpty()){
                            lessonNameEditTextScheduleSettings.setError("Введите текст");
                            return;
                        }
                        if(teacherName.isEmpty()){
                            teacherNameScheduleSettings.setError("Введите текст");
                            return;
                        }
                        lessonDescription lessonDescription = new lessonDescription(lessonName, teacherName);

                        viewModel.addLessonDescription(lessonDescription);

                        doneLessonDescriptionSettingsSchedule.setVisibility(View.GONE);
                        lessonDescriptionContainerScheduleSettings.setVisibility(View.GONE);
                        addLessonDescriptionScheduleSettings.setVisibility(View.VISIBLE);
                        lessonNameEditTextScheduleSettings.setText("");
                        teacherNameScheduleSettings.setText("");

                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    }
                });
            }
        });
    }


    private void getLessons() {
        viewModel.getLessonsDescription();
    }




    private void showLessonsInListView(ArrayList<lessonDescription> listLessons){
        adapter = new AdapterListLessonsDescription(getApplicationContext(), listLessons);

        ListViewForListOfLessonsDescriptionScheduleSettings.setAdapter(adapter);

        ListViewForListOfLessonsDescriptionScheduleSettings.setClickable(true);
        ListViewForListOfLessonsDescriptionScheduleSettings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lessonDescription lessonDescription = adapter.getItem(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(changeLessonsForSchedule.this);

                View customView = getLayoutInflater().inflate(R.layout.alert_dialog_exit, null);
                Button buttonConfirm = customView.findViewById(R.id.buttonCustomLogOutAlertDialogConfirm);
                Button buttonCancel = customView.findViewById(R.id.buttonCustomLogOutAlertDialogCancel);

                buttonConfirm.setText("Удалить");
                buttonCancel.setText("Отмена");
                ((TextView) customView.findViewById(R.id.textCustomLogOutAlertDialog)).setText("Вы уверены что хотите удалить " + lessonDescription.subjectName);

                builder.setView(customView);

                AlertDialog alertDialog = builder.create();

                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        View customViewForAnim = alertDialog.getWindow().getDecorView();
                        int height = customViewForAnim.getHeight();
                        int width = customViewForAnim.getWidth();

                        Animator animation =  ViewAnimationUtils.createCircularReveal(
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

                buttonConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewModel.deleteLessonDescription(lessonDescription);
                        alertDialog.cancel();
                    }
                });
                buttonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });
            }
        });
    }

}