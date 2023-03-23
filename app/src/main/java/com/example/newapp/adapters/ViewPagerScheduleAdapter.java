package com.example.newapp.adapters;


import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newapp.R;
import com.example.newapp.domain.models.arrayListForSchedule;
import com.example.newapp.domain.models.lesson;
import com.example.newapp.interfaces.adapterOnBindViewHolder;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;

import java.util.HashMap;
import java.util.Map;

public class ViewPagerScheduleAdapter extends RecyclerView.Adapter<ViewPagerScheduleAdapter.PagerViewHolder> {

    private String[] daysOfWeek;
    private FirebaseFirestore fStore;

    private adapterOnBindViewHolder callback;

    private Map<String, arrayListForSchedule> weekLessonsList = new HashMap<>();


    public ViewPagerScheduleAdapter(String[] daysOfWeek, adapterOnBindViewHolder callback) {
        this.daysOfWeek = daysOfWeek;
        this.callback = callback;
    }

    @NonNull
    @Override
    public PagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        weekLessonsList.put("test", null);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_screen, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ((RecyclerView) view.findViewById(R.id.RecyclerViewInCustomScreen)).setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return new PagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PagerViewHolder holder, int position) {
        Log.d("Aboba", "bind");
        if (weekLessonsList.get(daysOfWeek[position]) == null) {
            Log.d("Aboba", "Запрос на получения по дню недели " + daysOfWeek[position]);
            callback.callback(daysOfWeek[position]);
            weekLessonsList.put(daysOfWeek[position], new arrayListForSchedule()); //затычка чтобы после вызова notifyDataSetChanged не вызвались заново вызовы бд
        } else {
            arrayListForSchedule lessonList = weekLessonsList.get(daysOfWeek[position]);
            if (!lessonList.isEmpty()) {
                holder.bind(lessonList);
            } else {
                holder.showEmptyMessage();
            }
        }
    }

    @Override
    public int getItemCount() {
        return daysOfWeek.length;
    }

    public void addNewDayLessons(arrayListForSchedule lessons) {
        weekLessonsList.put(lessons.getDayOfWeek(), lessons);
        Log.d("Aboba", "добавление нового расписания на день недели " + lessons.getDayOfWeek() + "   со знач " + lessons.toString());
        notifyDataSetChanged();
    }


    class PagerViewHolder extends RecyclerView.ViewHolder {

        private RecyclerView recView;

        public PagerViewHolder(@NonNull View itemView) {
            super(itemView);
            recView = itemView.findViewById(R.id.RecyclerViewInCustomScreen);
        }

        public void bind(ArrayList<lesson> list) {
            RecAdapterListLessons adapter = new RecAdapterListLessons(list);
            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
            recView.setLayoutManager(layoutManager);
            recView.setHasFixedSize(true);
            recView.setAdapter(adapter);
        }

        public void showEmptyMessage() {
            showEmptyMessageRecViewAdapter adapter = new showEmptyMessageRecViewAdapter();
            recView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            recView.setHasFixedSize(true);
            recView.setAdapter(adapter);
        }
    }
}