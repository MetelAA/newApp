package com.example.newapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.newapp.R;

import com.example.newapp.core.GroupUserForListView;
import com.example.newapp.core.Lesson;

import java.util.ArrayList;

public class customListLessonsArrayAdapter extends ArrayAdapter<Lesson> {

    public customListLessonsArrayAdapter(Context context, ArrayList<Lesson> dataAdapter){
        super(context, R.layout.custom_list_item_for_lesson_schedule, dataAdapter);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Lesson lesson = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_item_for_lesson_schedule, parent, false);
        }

        TextView lessonNumber = convertView.findViewById(R.id.lessonNumberShowSchedule);
        TextView lessonName = convertView.findViewById(R.id.lessonNameShowSchedule);
        TextView lessonTime = convertView.findViewById(R.id.lessonTimeShowSchedule);



        lessonNumber.setText(String.valueOf(lesson.number) + " Урок:");
        lessonName.setText(lesson.name);
        lessonTime.setText(lesson.time);

        return convertView;

    }
}

