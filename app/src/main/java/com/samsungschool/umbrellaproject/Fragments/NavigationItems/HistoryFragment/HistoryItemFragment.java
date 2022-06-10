package com.samsungschool.umbrellaproject.Fragments.NavigationItems.HistoryFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.samsungschool.umbrellaproject.databinding.FragmentHistoryitemBinding;

public class HistoryItemFragment extends Fragment {
    FragmentHistoryitemBinding binding;
    public static HistoryItemFragment newInstance(HistoryItem historyItem) {

        Bundle args = new Bundle();
        args.putParcelable("item", historyItem);
        HistoryItemFragment fragment = new HistoryItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        HistoryItem historyItem = ((HistoryItem)getArguments().getParcelable("item"));
        String title = (historyItem.getDate() + ", " + historyItem.getTime());
        binding = FragmentHistoryitemBinding.inflate(getLayoutInflater());
        binding.toolbar.setTitle(title);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });
        Log.i("title info", title);
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
