package com.example.newapp.ui.news;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.newapp.databinding.FragmentNewsDialogBinding;
import com.example.newapp.domain.models.eventData;
import com.example.newapp.global.constants;

import java.time.LocalDate;

public class newsDialogFragment extends DialogFragment{

    private FragmentNewsDialogBinding binding;
    private newsDialogViewModelImpl viewModel;
    private EditText eventTitle, eventDescription, eventStartTime, eventEndTime;
    private CheckBox isFullDayEventCheckBox;
    private ImageButton done;

    private LocalDate dialogDate;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNewsDialogBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(newsDialogViewModelImpl.class);

        eventTitle = binding.eventTitleNewsDialog;
        eventDescription = binding.eventDescriptionNewsDialog;
        eventStartTime = binding.eventStartTimeNewsDialog;
        eventEndTime = binding.eventEndTimeNewsDialog;
        isFullDayEventCheckBox = binding.isFullDayCheckBoxDialogNews;
        done = binding.doneNewsDialog;

        //getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogDate = (LocalDate) getArguments().getSerializable(constants.SERIALIZABLE_DATE_BUNDLE_KEY);

        setListeners();
        setObservers();
        return binding.getRoot();
    }

    private void setObservers() {
        viewModel.onError.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                getDialog().cancel();
                viewModel.onError.removeObservers(getViewLifecycleOwner());
                viewModel.onAdded.removeObservers(getViewLifecycleOwner());
            }
        });
        viewModel.onAdded.observe(getViewLifecycleOwner(), new Observer<eventData>() {
            @Override
            public void onChanged(eventData eventData) {
                getDialog().cancel();
                Bundle bundle = new Bundle();
                bundle.putSerializable(constants.SERIALIZABLE_EVENT_DATA_BUNDLE_KEY, eventData);
                bundle.putSerializable(constants.SERIALIZABLE_DATE_BUNDLE_KEY, dialogDate);
                getParentFragmentManager().setFragmentResult(constants.NEWS_DIALOG_FRAGMENT_RESULT_KEY, bundle);
                viewModel.onError.removeObservers(getViewLifecycleOwner());
                viewModel.onAdded.removeObservers(getViewLifecycleOwner());
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setListeners() {
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventData eventData;
                if(isFullDayEventCheckBox.isChecked()){
                    eventData = new eventData(
                            eventTitle.getText().toString(),
                            eventDescription.getText().toString(),
                            true
                    );
                }else{
                    eventData = new eventData(
                            eventTitle.getText().toString(),
                            eventDescription.getText().toString(),
                            eventStartTime.getText().toString(),
                            eventEndTime.getText().toString(),
                            false
                    );
                }
                viewModel.setNewDayEvent(eventData, dialogDate);
            }
        });
        isFullDayEventCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeTimeFields(isChecked);
            }
        });
    }

    private void changeTimeFields(Boolean flag){
        if(flag){
            eventStartTime.setVisibility(View.GONE);
            eventEndTime.setVisibility(View.GONE);
        }else{
            eventStartTime.setVisibility(View.VISIBLE);
            eventEndTime.setVisibility(View.VISIBLE);
        }
    }
}