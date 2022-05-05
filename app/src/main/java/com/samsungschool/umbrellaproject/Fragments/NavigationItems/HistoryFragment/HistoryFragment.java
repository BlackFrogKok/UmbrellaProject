package com.samsungschool.umbrellaproject.Fragments.NavigationItems.HistoryFragment;

import android.icu.util.LocaleData;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.samsungschool.umbrellaproject.R;
import com.samsungschool.umbrellaproject.databinding.FragmentHistoryBinding;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
    HistoryAdapter historyAdapter;
    FragmentHistoryBinding binding;
    List<HistoryItem> historyItems = new ArrayList<HistoryItem>();
    public static HistoryFragment newInstance() {

        Bundle args = new Bundle();

        HistoryFragment fragment = new HistoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        historyItems.add(new HistoryItem("Улица им. Федора Седова", "Аренда все еще идет", "20.11.2004"));
        historyItems.add(new HistoryItem("Улица Самсунга", "14 часов 32 минуты", "12.04.1924"));
        historyItems.add(new HistoryItem("Улица Покровка", "13 часов 32 минуты", "12.04.1923"));
        historyItems.add(new HistoryItem("Улица им. Нурлана", "14 часов 32 минуты", "12.04.1924"));
        historyItems.add(new HistoryItem("Улица им. Файербэйза", "14 часов 32 минуты", "12.04.1324"));
        historyItems.add(new HistoryItem("Улица им. Андроида", "15 часов 32 минуты", "12.04.924"));
        historyItems.add(new HistoryItem("Улица им. Хуевого Api от яндекса", "14 часов 32 минуты", "12.04.1924"));
        historyItems.add(new HistoryItem("Улица им. Хорошего Api от яндекса", "14 часов 32 минуты", "12.04.1924"));
        historyItems.add(new HistoryItem("Улица Котлина", "16 часов 32 минуты", "12.04.1924"));
        historyItems.add(new HistoryItem("Улица Джавы", "14 часов 32 минуты", "12.04.1924"));



        binding = FragmentHistoryBinding.inflate(getLayoutInflater());
        RecyclerView recyclerView = binding.recyclerView;
        historyAdapter = new HistoryAdapter(historyItems);
        recyclerView.setAdapter(historyAdapter);
        setToolBarListener();
        return binding.getRoot();
    }

    private void setToolBarListener(){
        binding.toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        binding.toolbar.getMenu().findItem(R.id.clear).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                historyItems.clear();
                historyAdapter.notifyChanged();
                return false;
            }
        });
    }
}
