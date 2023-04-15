package com.example.newapp.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newapp.R;
import com.example.newapp.domain.models.dayEventsData;
import com.example.newapp.interfaces.adapterOnClickInterface;

import java.util.ArrayList;

public class calendarViewAdapter extends RecyclerView.Adapter<calendarViewAdapter.viewHolder> {



    private ArrayList<dayEventsData> monthEventsList = new ArrayList<>();
    private adapterOnClickInterface callback;

    public calendarViewAdapter(ArrayList<dayEventsData> monthEventsList, adapterOnClickInterface callback) {
        this.monthEventsList = monthEventsList;
        this.callback = callback;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_for_calendar_view, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.bind(monthEventsList.get(position));
    }

    @Override
    public int getItemCount() {
        return monthEventsList.size();
    }

    class viewHolder extends RecyclerView.ViewHolder{

        private ConstraintLayout layoutCalendarView;
        private TextView date, mainEvent, anothEventCount;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            layoutCalendarView = itemView.findViewById(R.id.layoutCalendarView);
            date = itemView.findViewById(R.id.dateCalendarView);
            mainEvent = itemView.findViewById(R.id.mainEventCalendarView);
            anothEventCount = itemView.findViewById(R.id.anotherEventsCount);

            layoutCalendarView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.callback(monthEventsList.get(getAdapterPosition()));
                }
            });
        }

        public void bind(dayEventsData dayEvent){
            if(dayEvent != null){
                date.setText(dayEvent.dayDate);
                if(dayEvent.events != null){
                    ///Log.d("Aboba", "event not null");
                    mainEvent.setText(dayEvent.events.get(0).eventTitle);
                    if(dayEvent.events.size() - 1 > 0){
                        anothEventCount.setText("+ " + (dayEvent.events.size() - 1)+ " события");
                    }else{
                        anothEventCount.setVisibility(View.INVISIBLE);
                    }
                }else{
                    mainEvent.setVisibility(View.INVISIBLE);
                    anothEventCount.setVisibility(View.INVISIBLE);
                }
            }else{
                layoutCalendarView.setVisibility(View.INVISIBLE);
                //Log.d("Aboba", "event null");
            }
        }
    }
}
