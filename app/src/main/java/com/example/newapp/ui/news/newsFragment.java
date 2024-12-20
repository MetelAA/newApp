package com.example.newapp.ui.news;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.newapp.R;
import com.example.newapp.adapters.adapterShowDayEvents;
import com.example.newapp.adapters.calendarViewAdapter;
import com.example.newapp.adapters.showEmptyMessageRecViewAdapter;
import com.example.newapp.databinding.FragmentNewsBinding;
import com.example.newapp.domain.models.dayEventsData;
import com.example.newapp.domain.models.eventData;
import com.example.newapp.global.User;
import com.example.newapp.global.constants;
import com.example.newapp.interfaces.adapterOnClickInterface;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class newsFragment extends Fragment {

    FragmentNewsBinding binding;
    newsFragmentViewModelImpl viewModel;

    private ConstraintLayout mainElem;
    private ImageButton monthBackBtn, monthForwardBtn;
    private RecyclerView recyclerView;

    private TextView monthYearText;

    private LocalDate selectedDate;

    private calendarViewAdapter adapter;

    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout bottomSheetMainElem;
    private TextView dayTitleShowDayEvent;
    private ImageButton addNewEventShowDayEvents;
    private RecyclerView recyclerViewShowDayEvents;

    private ArrayList<dayEventsData> monthEvents = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNewsBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(newsFragmentViewModelImpl.class);

        mainElem = binding.newsMainElem;
        monthBackBtn = binding.newsMonthBackBtn;
        monthForwardBtn = binding.newsMonthForwardBtn;
        recyclerView = binding.newsRecView;
        monthYearText = binding.newsMonthYearText;
        bottomSheetMainElem = binding.getRoot().findViewById(R.id.bottomSheetShowDayEventsMainElem);
        dayTitleShowDayEvent = bottomSheetMainElem.findViewById(R.id.dayTitleShowDayEvent);
        addNewEventShowDayEvents = bottomSheetMainElem.findViewById(R.id.addNewEventShowDayEvents);
        recyclerViewShowDayEvents = bottomSheetMainElem.findViewById(R.id.recyclerViewShowDayEvents);

        selectedDate = LocalDate.now();

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetMainElem);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        initFragment();
        getMonthEvents();
        fillMonthEventsList();
        setListeners();
        setObservers();
        return binding.getRoot();
    }

    private void initFragment() {
        monthYearText.setText(formatDateToYearMonth(selectedDate));
    }

    private final DateTimeFormatter formatterDateToYearMonth = DateTimeFormatter.ofPattern("LLLL yyyy").withLocale(new Locale("ru"));
    private final DateTimeFormatter formatterDayMonthDayOfWeek = DateTimeFormatter.ofPattern("dd MMMM, EEEE").withLocale(new Locale("ru"));

    private String formatDateToYearMonth(LocalDate localDate) {
        String trueFormat = formatterDateToYearMonth.format(localDate);
        return trueFormat.substring(0, 1).toUpperCase() + trueFormat.substring(1);
    }

    private String formatDataToDayMonthDayOfWeek(LocalDate localDate) {
        return formatterDayMonthDayOfWeek.format(localDate);
    }


    private void setListeners() {
        monthBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousMonthAction();
            }
        });

        monthForwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMonthAction();
            }
        });
    }

    private void getMonthEvents() {
        viewModel.getMonthEvents(selectedDate);
    }

    private void fillMonthEventsList() {
        YearMonth yearMonth = YearMonth.from(selectedDate);
        monthEvents.clear();

        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for (int i = 1; i <= 42; i++) {
            if (i < dayOfWeek || i >= (daysInMonth + dayOfWeek)) {
                monthEvents.add(null);
            } else {
                monthEvents.add(new dayEventsData(new ArrayList<>(), String.valueOf(i - dayOfWeek + 1)));
            }
        }
        showRecView();
    }

    private void showRecView() {
        adapter = new calendarViewAdapter(monthEvents, new adapterOnClickInterface() {
            @Override
            public void callback(Object data) {
                showBottomSheet((dayEventsData) data);
            }
        });
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }


    private void showBottomSheet(dayEventsData data) {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        dayTitleShowDayEvent.setText(formatDataToDayMonthDayOfWeek(LocalDate.of(selectedDate.getYear(), selectedDate.getMonthValue(), Integer.parseInt(data.dayDate))));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewShowDayEvents.setLayoutManager(layoutManager);
        recyclerViewShowDayEvents.setHasFixedSize(true);

        if (data.events != null) {
            adapterShowDayEvents adapterShowDayEvents = new adapterShowDayEvents(data.events, User.getType(), new adapterOnClickInterface() {
                @Override
                public void callback(Object data) {
                    if(data instanceof Boolean){
                        setEventDeleteListener();
                    }
                }
            });
            recyclerViewShowDayEvents.setAdapter(adapterShowDayEvents);
        } else {
            showEmptyMessageRecViewAdapter showEmptyMessage = new showEmptyMessageRecViewAdapter();
            recyclerViewShowDayEvents.setAdapter(showEmptyMessage);
        }

        addNewEventShowDayEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalDate dialogDate = selectedDate;
                dialogDate = dialogDate.withDayOfMonth(Integer.parseInt(data.dayDate));

                newsDialogFragment myDialogFragment = new newsDialogFragment();
                FragmentManager manager = getParentFragmentManager();

                Bundle args = new Bundle();
                args.putSerializable(constants.SERIALIZABLE_DATE_BUNDLE_KEY, dialogDate);
                myDialogFragment.setArguments(args);

                FragmentTransaction transaction = manager.beginTransaction();
                myDialogFragment.show(transaction, "dialog");
                manager.setFragmentResultListener(
                        constants.NEWS_DIALOG_FRAGMENT_RESULT_KEY,
                        getViewLifecycleOwner(),
                        new FragmentResultListener() {
                            @Override
                            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                                addEventInList(
                                        (eventData) result.getSerializable(constants.SERIALIZABLE_EVENT_DATA_BUNDLE_KEY),
                                        ((LocalDate)result.getSerializable(constants.SERIALIZABLE_DATE_BUNDLE_KEY)).getDayOfMonth()
                                    );
                            }
                        });
            }
        });
    }

    private void setEventDeleteListener(){
        addNewEventShowDayEvents.setBackgroundResource(R.drawable.ic_baseline_delete);
        addNewEventShowDayEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<eventData> dayEvents = new ArrayList<>();
                adapterShowDayEvents adapter = (adapterShowDayEvents) recyclerViewShowDayEvents.getAdapter();
                for (int childCount = recyclerViewShowDayEvents.getChildCount(), i = 0; i < childCount; ++i) {
                    final adapterShowDayEvents.viewHolder holder = (adapterShowDayEvents.viewHolder) recyclerViewShowDayEvents.getChildViewHolder(recyclerViewShowDayEvents.getChildAt(i));
                    if(holder.checkBox.isChecked()){
                        dayEvents.add(adapter.events.get(i));
                    }
                }
                Log.d("Aboba", "selected day events " + dayEvents.toString());
            }
        });
    }

    private void addEventInList(eventData data, int dayOfMonth){
        String dayDate = String.valueOf(dayOfMonth);
        for (int i = 0; i < monthEvents.size(); i++) {
            if (monthEvents.get(i) != null) {
                if (TextUtils.equals(monthEvents.get(i).dayDate, dayDate)) {
                    monthEvents.get(i).events.add(data);
                    adapter.notifyItemChanged(i);
                }
            }
        }
    }

    private void nextMonthAction() {
        selectedDate = selectedDate.plusMonths(1);
        fillMonthEventsList();
        getMonthEvents();
        initFragment();
    }

    private void previousMonthAction() {
        selectedDate = selectedDate.minusMonths(1);
        fillMonthEventsList();
        getMonthEvents();
        initFragment();
    }

    private void setObservers() {
        viewModel.onErrorLiveData.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d("Aboba", s);
                Snackbar.make(mainElem, s, Snackbar.ANIMATION_MODE_SLIDE).show();
            }
        });

        viewModel.gotMonthEvents.observe(getViewLifecycleOwner(), new Observer<Map<String, dayEventsData>>() {
            @Override
            public void onChanged(Map<String, dayEventsData> dayEventsDataMap) {
                //Log.d("Aboba", "onChanged  " + dayEventsDataMap);
                for (int i = 0; i < monthEvents.size(); i++) {
                    Log.d("Aboba", "i:   " + i + "      " + monthEvents.get(i));
                    if (monthEvents.get(i) != null) {
                        if (dayEventsDataMap.get(monthEvents.get(i).dayDate) != null) {
                            monthEvents.set(i, dayEventsDataMap.get(monthEvents.get(i).dayDate));
                            Log.d("Aboba", "not null        " + dayEventsDataMap.get(monthEvents.get(i).dayDate).toString());
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }



}