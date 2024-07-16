package com.example.newapp.adapters;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newapp.R;
import com.example.newapp.domain.models.eventData;
import com.example.newapp.interfaces.adapterOnClickInterface;

import java.util.ArrayList;

import io.grpc.internal.ConscryptLoader;

public class adapterShowDayEvents extends RecyclerView.Adapter<adapterShowDayEvents.viewHolder> {
    public ArrayList<eventData> events = new ArrayList<>();
    private String userType;
    private Boolean isDeleteActive = false;
    private adapterOnClickInterface callback;


    public adapterShowDayEvents(ArrayList<eventData> events, String userType, adapterOnClickInterface callback) {
        this.events = events;
        this.userType = userType;
        this.callback = callback;
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

        public final TextView startTime, endTime, title, description;
        public final ConstraintLayout mainElem;
        public final CheckBox checkBox;

        public viewHolder(@NonNull View view) {
            super(view);
            Log.d("Aboba", view + " ");
            startTime = view.findViewById(R.id.eventStartTimeShowDayEvent);
            endTime = view.findViewById(R.id.eventEndTimeShowDayEvent);
            title = view.findViewById(R.id.eventTitleShowDayEvent);
            description = view.findViewById(R.id.eventDescriptionShowDayEvent);
            mainElem = view.findViewById(R.id.mainElemShowDayEvents);
            checkBox = view.findViewById(R.id.checkBoxShowDayEvent);
            Log.d("Aboba", startTime.toString() + endTime.toString() + title.toString() + description.toString() + "     ");
            if(TextUtils.equals(userType, "Учитель")) {
                mainElem.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            if (isDeleteActive) {
                                checkBox.setChecked(!checkBox.isChecked());
                                return true;
                            }
                            isDeleteActive = true;
                            callback.callback(true);
                            notifyDataSetChanged();
                        }
                        return true;
                    }
                });
            }
        }

        public void bind(eventData data){
            Log.d("Aboba", data.toString());
            title.setText(data.eventTitle);
            description.setText(data.eventDescription);
            if(data.isFullDay){
                startTime.setText("Весь день");
                endTime.setVisibility(View.GONE);
            }else{
                endTime.setVisibility(View.VISIBLE);
                startTime.setText(data.eventStartTime);
                endTime.setText(data.eventEndTime);
            }
            if(isDeleteActive){
                checkBox.measure(0, 0);
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setTranslationX(-checkBox.getMeasuredWidth());
                checkBox.animate()
                    .translationX(0)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setDuration(500)
                    .start();

            }else{
                checkBox.setChecked(false);
                checkBox.setVisibility(View.GONE);
            }
        }
    }
}
