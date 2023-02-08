package com.example.newapp.adapters;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newapp.R;
import com.example.newapp.domain.models.oldModels.LessonForShowSchedule;
import com.example.newapp.data.getDayLessons;
import com.example.newapp.interfaces.CallbackInterfaceWithList;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;

import java.util.HashMap;
import java.util.Map;

public class ViewPagerScheduleAdapter extends RecyclerView.Adapter<ViewPagerScheduleAdapter.PagerViewHolder> {

    private String[] daysOfWeek;
    private FirebaseFirestore fStore;

    private Map<String, ArrayList<LessonForShowSchedule>> weekLessonsList = new HashMap<>();


    public ViewPagerScheduleAdapter(String[] daysOfWeek, FirebaseFirestore fStore) {
        this.daysOfWeek = daysOfWeek;
        this.fStore = fStore;
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
        if(weekLessonsList.get(daysOfWeek[position]) == null){
            int fixedPosition = position;
            getDayLessons getDayLessons = new getDayLessons(new CallbackInterfaceWithList() {
                @Override
                public void requestResult(ArrayList list) {
                    if(list.isEmpty()){
                        holder.showEmptyMessage();
                    }else {
                        weekLessonsList.put(daysOfWeek[fixedPosition], list);
                        holder.bind(weekLessonsList.get(daysOfWeek[fixedPosition]));
                    }
                }
                @Override
                public void throwError(String error) {
                    Snackbar.make(holder.recView, "Ошибка! Не удалось получить расписание", Snackbar.LENGTH_LONG).show();
                }
            });
            getDayLessons.getLessons(fStore, daysOfWeek[fixedPosition]);
        }else{
            holder.bind(weekLessonsList.get(daysOfWeek[position]));
        }
    }

    @Override
    public int getItemCount() {
        return daysOfWeek.length;
    }


    class PagerViewHolder extends RecyclerView.ViewHolder{

        private RecyclerView recView;

        public PagerViewHolder(@NonNull View itemView) {
            super(itemView);
            recView = itemView.findViewById(R.id.RecyclerViewInCustomScreen);
        }

        public void bind(ArrayList<LessonForShowSchedule> list) {
            RecAdapterListLessons adapter = new RecAdapterListLessons(list);
            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
            recView.setLayoutManager(layoutManager);
            recView.setHasFixedSize(true);
            recView.setAdapter(adapter);
        }

        public void showEmptyMessage(){
            showEmptyMessageRecViewAdapter adapter = new showEmptyMessageRecViewAdapter();
            recView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            recView.setHasFixedSize(true);
            recView.setAdapter(adapter);
        }
    }
}