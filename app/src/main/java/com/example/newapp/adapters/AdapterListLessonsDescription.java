package com.example.newapp.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.newapp.R;
import com.example.newapp.domain.models.lessonDescription;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AdapterListLessonsDescription extends ArrayAdapter<lessonDescription> {

    public AdapterListLessonsDescription(Context context, ArrayList<lessonDescription> dataAdapter){
        super(context, R.layout.list_item_with_two_vertical_fields_and_btn, dataAdapter);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        lessonDescription lesson = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_with_two_vertical_fields_and_btn, parent, false);
        }

        TextView subject = convertView.findViewById(R.id.nameTextInCustomListViewWithTwoTextFieldsAndBtn);
        TextView teacherName = convertView.findViewById(R.id.emailTextInCustomListViewWithTwoTextFieldsAndBtn);
        ((TextView) convertView.findViewById(R.id.userTypeTextInCustomListViewWithTwoTextFieldsAndBtn)).setVisibility(View.GONE);

        ImageView imageViewInCustomListView = convertView.findViewById(R.id.imageViewInCustomListViewWithTwoTextFieldsAndBtn);
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_baseline_delete);
        imageViewInCustomListView.setImageDrawable(drawable);

        subject.setText(lesson.subjectName);
        teacherName.setText(lesson.teacherName);

        return convertView;

    }
}
