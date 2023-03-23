package com.example.newapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newapp.R;
import com.example.newapp.domain.models.lesson;

import java.util.ArrayList;
import java.util.List;

public class RecAdapterListLessons extends RecyclerView.Adapter<RecAdapterListLessons.ListLessonsViewHolder>{

    private List<lesson> lessonsForSchedule;

    public RecAdapterListLessons(ArrayList<lesson> list) {
        lessonsForSchedule = list;
    }

    @NonNull
    @Override
    public ListLessonsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_schedule_list_item, parent, false);
        return new ListLessonsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListLessonsViewHolder holder, int position) {
        holder.bind(lessonsForSchedule.get(position));
    }

    @Override
    public int getItemCount() {
        return lessonsForSchedule.size();
    }

    class ListLessonsViewHolder extends RecyclerView.ViewHolder{

        private TextView lessonName, time, teacher, studyRoom;


        public ListLessonsViewHolder(@NonNull View itemView) {
            super(itemView);
            lessonName = itemView.findViewById(R.id.lessonNameShowScheduleItem);
            time = itemView.findViewById(R.id.lessonTimeShowScheduleItem);
            teacher = itemView.findViewById(R.id.teacherNameShowScheduleItem);
            studyRoom = itemView.findViewById(R.id.studyRoomShowScheduleItem);
        }

        public void bind(lesson lesson){
            lessonName.setText(lesson.subject);
            time.setText(lesson.timeStart + " - " + lesson.timeEnd);
            teacher.setText(lesson.teacher);
            studyRoom.setText(lesson.studyRoom + " кабинет");
        }
    }
}
