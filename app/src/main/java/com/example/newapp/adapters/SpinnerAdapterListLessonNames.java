package com.example.newapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.newapp.R;
import com.example.newapp.domain.models.oldModels.LessonForScheduleSettings;

import java.util.ArrayList;
public class SpinnerAdapterListLessonNames extends ArrayAdapter<LessonForScheduleSettings> {
    public SpinnerAdapterListLessonNames(Context context, ArrayList<LessonForScheduleSettings> lessons){
        super(context, R.layout.selected_spinner_list_item, lessons);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LessonForScheduleSettings lesson = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.selected_spinner_list_item, parent, false);
        }
        TextView lessonName = convertView.findViewById(R.id.lessonNameTextInSelectedListItemForSpinner);
        lessonName.setText(lesson.subjectName);

        return  convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.spinner_list_item, parent, false);
        LessonForScheduleSettings lesson = getItem(position);


        TextView subjectName = view.findViewById(R.id.lessonNameTextInListItemForSpinner);
        TextView teacherName = view.findViewById(R.id.teacherNameTextInListItemForSpinner);


        subjectName.setText(lesson.subjectName);
        teacherName.setText(lesson.teacherName);

        return view;
    }
}
