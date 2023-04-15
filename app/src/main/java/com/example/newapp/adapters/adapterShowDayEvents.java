package com.example.newapp.adapters;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newapp.R;
import com.example.newapp.domain.models.eventData;

import java.util.ArrayList;

import kotlin.jvm.internal.Lambda;

public class adapterShowDayEvents extends RecyclerView.Adapter<adapterShowDayEvents.viewHolder> {
    ArrayList<eventData> events = new ArrayList<>();

    public adapterShowDayEvents(ArrayList<eventData> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_for_show_day_events, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.bind(events.get(position));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        private final TextView startTime, endTime, title, description;

        public viewHolder(@NonNull View view) {
            super(view);
            Log.d("Aboba", view + " ");
            startTime = view.findViewById(R.id.eventStartTimeShowDayEvent);
            endTime = view.findViewById(R.id.eventEndTimeShowDayEvent);
            title = view.findViewById(R.id.eventTitleShowDayEvent);
            description = view.findViewById(R.id.eventDescriptionShowDayEvent);
            Log.d("Aboba", startTime.toString() + endTime.toString() + title.toString() + description.toString() + "     ");
        }

        public void bind(eventData data){
            Log.d("Aboba", data.toString());
            title.setText(data.eventTitle);
            description.setText(data.eventDescription);
            startTime.setText(data.eventStartTime);
            if(data.eventEndTime == null){
                endTime.setVisibility(View.GONE);
            }else{
                endTime.setVisibility(View.VISIBLE);
                endTime.setText(data.eventEndTime);
            }
        }
    }
}
