package com.samsungschool.umbrellaproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.budiyev.android.codescanner.CodeScanner;
import com.samsungschool.umbrellaproject.databinding.FragmentQrBinding;

public class QrFragment extends Fragment {

    private CodeScanner mCodeScanner;

    public static QrFragment newInstance() {
        Bundle args = new Bundle();
        QrFragment fragment = new QrFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentQrBinding binding = FragmentQrBinding.inflate(getLayoutInflater());
        mCodeScanner = new CodeScanner(requireActivity(), binding.qrCamera);
        mCodeScanner.setDecodeCallback(result -> Toast.makeText(getContext(), result.getText(), Toast.LENGTH_SHORT).show());
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }
}

