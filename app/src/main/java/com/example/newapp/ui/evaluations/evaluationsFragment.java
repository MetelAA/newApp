package com.example.newapp.ui.evaluations;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.newapp.adapters.calendarViewAdapter;
import com.example.newapp.databinding.FragmentEvaluationsBinding;
import com.example.newapp.domain.models.dayEventsData;
import com.example.newapp.interfaces.adapterOnClickInterface;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class evaluationsFragment extends Fragment {
    FragmentEvaluationsBinding binding;
    evaluationsViewModelImpl viewModel;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEvaluationsBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(evaluationsViewModelImpl.class);


        return binding.getRoot();
    }
}