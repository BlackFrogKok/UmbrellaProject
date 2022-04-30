package com.samsungschool.umbrellaproject.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.samsungschool.umbrellaproject.databinding.FragmentAboutBinding;

public class AboutFragment extends Fragment {

    FragmentAboutBinding binding;
    public static AboutFragment newInstance() {
        AboutFragment fragment = new AboutFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAboutBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        binding = null;
        super.onDestroy();
    }
}
