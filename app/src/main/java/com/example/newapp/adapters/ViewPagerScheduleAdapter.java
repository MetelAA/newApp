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


import java.time.DayOfWeek;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Map;

public class ViewPagerScheduleAdapter extends RecyclerView.Adapter<ViewPagerScheduleAdapter.PagerViewHolder> {

    private DayOfWeek[] daysOfWeek1 = DayOfWeek.values();

    private adapterOnBindViewHolder callback;

    private Map<String, arrayListForSchedule> weekLessonsMap = new HashMap<>();


    public ViewPagerScheduleAdapter(adapterOnBindViewHolder callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public PagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        weekLessonsMap.put("test", null);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_screen, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ((RecyclerView) view.findViewById(R.id.RecyclerViewInCustomScreen)).setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return new PagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PagerViewHolder holder, int position) {
        Log.d("Aboba", "bind day " + daysOfWeek1[position]);
        if (weekLessonsMap.get(daysOfWeek1[position].toString()) == null) {
            Log.d("Aboba", "Запрос на получения по дню недели " + daysOfWeek1[position]);
            callback.callback(daysOfWeek1[position].toString());
//            weekLessonsMap.put(daysOfWeek[position], new arrayListForSchedule()); //затычка чтобы после вызова notifyDataSetChanged не вызвались заново вызовы бд
        } else {
            arrayListForSchedule lessonList = weekLessonsMap.get(daysOfWeek1[position].toString());
            assert lessonList != null;
            if (!lessonList.isEmpty()) {
                holder.bind(lessonList);
            } else {
                holder.showEmptyMessage();
            }
        }
    }

    @Override
    public int getItemCount() {
        return daysOfWeek1.length;
    }

    public void addNewDayLessons(arrayListForSchedule lessons) {
        weekLessonsMap.put(lessons.getDayOfWeek(), lessons);
        for (int i = 0; i < daysOfWeek1.length; i++) {
            if(TextUtils.equals(lessons.getDayOfWeek(), daysOfWeek1[i].toString())){
                notifyItemChanged(i);
            }
        }
        //Log.d("Aboba", "добавление нового расписания на день недели " + lessons.getDayOfWeek() + "   со знач " + lessons.toString());

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