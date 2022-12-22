package com.example.newapp.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.newapp.R;
import com.example.newapp.adapters.customAdapterListLessonsDescription;
import com.example.newapp.core.LessonForScheduleSettings;
import com.example.newapp.core.db.addDeleteLessonDescriptionToDB;
import com.example.newapp.core.db.getLessonDescription;
import com.example.newapp.databinding.ActivityGroupSettingsBinding;
import com.example.newapp.interfaces.CallbackInterface;
import com.example.newapp.interfaces.CallbackInterfaceWithList;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class groupSettingsActivity extends AppCompatActivity {

    FirebaseFirestore fStore;

    ActivityGroupSettingsBinding binding;
    ViewGroup mainElem;

    ImageButton doneLessonDescriptionSettingsSchedule;
    Button addLessonDescriptionScheduleSettings;
    LinearLayout lessonDescriptionContainerScheduleSettings;
    EditText lessonNameEditTextScheduleSettings;
    EditText teacherNameScheduleSettings;
    ListView ListViewForListOfLessonsDescriptionScheduleSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupSettingsBinding.inflate(getLayoutInflater());
        mainElem = binding.getRoot();
        fStore = FirebaseFirestore.getInstance();

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

                        Map<String, String> lessonData = new HashMap<>();
                        lessonData.put("subjectName", lessonName);
                        lessonData.put("teacherName", teacherName);



                        addDeleteLessonDescriptionToDB addDeleteLessonDescriptionToDB = new addDeleteLessonDescriptionToDB(new CallbackInterface() {
                            @Override
                            public void requestStatus(String status) {
                                Snackbar.make(mainElem, "Урок успешно добавлен", Snackbar.LENGTH_LONG).show();
                                showLessons();
                            }

                            @Override
                            public void throwError(String error) {
                                Snackbar.make(mainElem, error, Snackbar.LENGTH_LONG).show();
                            }
                        });
                        doneLessonDescriptionSettingsSchedule.setVisibility(View.GONE);
                        lessonDescriptionContainerScheduleSettings.setVisibility(View.GONE);
                        addLessonDescriptionScheduleSettings.setVisibility(View.VISIBLE);
                        lessonNameEditTextScheduleSettings.setText("");
                        teacherNameScheduleSettings.setText("");

                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                        addDeleteLessonDescriptionToDB.addLesson(fStore, lessonData);
                    }
                });
            }
        });
        showLessons();
        setContentView(mainElem);
    }



    private void showLessons() {
        getLessonDescription getLessons = new getLessonDescription(new CallbackInterfaceWithList() {
            @Override
            public void requestResult(ArrayList list) {
                showLessonsInListView(list);
            }

            @Override
            public void throwError(String error) {
                Snackbar.make(mainElem, error, Snackbar.LENGTH_LONG).show();
            }
        });

        getLessons.getLessonsFromDB(fStore);
    }




    private void showLessonsInListView(ArrayList<LessonForScheduleSettings> listLessons){
        customAdapterListLessonsDescription adapter = new customAdapterListLessonsDescription(getApplicationContext(), listLessons);

        ListViewForListOfLessonsDescriptionScheduleSettings.setAdapter(adapter);


        ListViewForListOfLessonsDescriptionScheduleSettings.setClickable(true);
        ListViewForListOfLessonsDescriptionScheduleSettings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String subjectName = ((TextView)view.findViewById(R.id.nameTextInCustomListViewWithTwoTextFieldsAndBtn)).getText().toString();

                AlertDialog.Builder builder = new AlertDialog.Builder(groupSettingsActivity.this);

                View customView = getLayoutInflater().inflate(R.layout.alert_dialog_exit, null);
                Button buttonConfirm = customView.findViewById(R.id.buttonCustomLogOutAlertDialogConfirm);
                Button buttonCancel = customView.findViewById(R.id.buttonCustomLogOutAlertDialogCancel);

                buttonConfirm.setText("Удалить");
                buttonCancel.setText("Отмена");
                ((TextView) customView.findViewById(R.id.textCustomLogOutAlertDialog)).setText("Вы уверены что хотите удалить " + subjectName);

                builder.setView(customView);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                buttonConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addDeleteLessonDescriptionToDB deleteLesson = new addDeleteLessonDescriptionToDB(new CallbackInterface() {
                            @Override
                            public void requestStatus(String status) {
                                //можеть быть только ок => проверок не требуется
                                adapter.remove(adapter.getItem(position));
                                adapter.notifyDataSetChanged();
                                alertDialog.cancel();
                            }

                            @Override
                            public void throwError(String error) {
                                Snackbar.make(mainElem, error, Snackbar.LENGTH_LONG).show();
                            }
                        });
                        deleteLesson.deleteLesson(fStore, subjectName);
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