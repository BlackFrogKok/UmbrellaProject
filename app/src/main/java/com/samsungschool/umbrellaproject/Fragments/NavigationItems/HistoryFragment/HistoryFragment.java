package com.samsungschool.umbrellaproject.Fragments.NavigationItems.HistoryFragment;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.samsungschool.umbrellaproject.R;
import com.samsungschool.umbrellaproject.databinding.FragmentHistoryBinding;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class HistoryFragment extends Fragment {
    private HistoryAdapter historyAdapter;
    private FragmentHistoryBinding binding;
    private List<HistoryItem> historyItems = new ArrayList<HistoryItem>(Arrays.asList(
            new HistoryItem("Улица им. Федора Седова", "Аренда все еще идет", "20.11.2004"),
        new HistoryItem("Улица Самсунга", "14 часов 32 минуты", "12.04.1924"),
        new HistoryItem("Улица Покровка", "13 часов 32 минуты", "12.04.1923"),
        new HistoryItem("Улица им. Нурлана", "14 часов 32 минуты", "12.04.1924"),
        new HistoryItem("Улица им. Файербэйза", "14 часов 32 минуты", "12.04.1324"),
        new HistoryItem("Улица им. Андроида", "15 часов 32 минуты", "12.04.924"),
        new HistoryItem("Улица им. Хуевого Api от яндекса", "14 часов 32 минуты", "12.04.1924"),
        new HistoryItem("Улица им. Хорошего Api от яндекса", "14 часов 32 минуты", "12.04.1924"),
        new HistoryItem("Улица Котлина", "16 часов 32 минуты", "12.04.1924"),
        new HistoryItem("Улица Джавы", "14 часов 32 минуты", "12.04.1924")
    ));

    public static HistoryFragment newInstance() {
        Bundle args = new Bundle();
        HistoryFragment fragment = new HistoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void getData(){
        historyAdapter.setHistoryItems(historyItems);
    }


    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        Animator anim = AnimatorInflater.loadAnimator(getActivity(), nextAnim);
        anim.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationEnd(Animator animation) {
                anim.removeListener(this);
                getData();
            }

            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}

        });

        return anim;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = binding.recyclerView;
        historyAdapter = new HistoryAdapter();
        recyclerView.setAdapter(historyAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setToolBarListener();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }


}
