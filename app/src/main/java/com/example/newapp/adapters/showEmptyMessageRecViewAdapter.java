package com.example.newapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newapp.R;

public class showEmptyMessageRecViewAdapter extends RecyclerView.Adapter<showEmptyMessageRecViewAdapter.viewHolder> {


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_message_rec_view, parent, false);
        return new viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class viewHolder extends RecyclerView.ViewHolder{
        public viewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
