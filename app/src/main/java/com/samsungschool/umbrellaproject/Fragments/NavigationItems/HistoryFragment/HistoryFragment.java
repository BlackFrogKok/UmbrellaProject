package com.samsungschool.umbrellaproject.Fragments.NavigationItems.HistoryFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.samsungschool.umbrellaproject.databinding.FragmentHistoryBinding;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
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
        for(int i = 0; i < 25; i++){
            historyItems.add(new HistoryItem(i));
        }


        binding = FragmentHistoryBinding.inflate(getLayoutInflater());
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setAdapter(new HistoryAdapter(historyItems));
        return binding.getRoot();
    }
}
