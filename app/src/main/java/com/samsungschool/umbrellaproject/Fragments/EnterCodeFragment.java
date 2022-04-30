package com.samsungschool.umbrellaproject.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.samsungschool.umbrellaproject.databinding.FragmentEnterCodeBinding;

public class EnterCodeFragment extends Fragment {

    private FragmentEnterCodeBinding binding;

    public static Fragment newFragment(){
        Fragment fragment = new EnterCodeFragment();
        Bundle arguments = new Bundle();
        fragment.setArguments(arguments);
        return fragment;
    }

    public interface onEnterCodeFragmentListener {
        void ContinueBtnCode(String code);
        void ResetBtnCode(String phone);
    }

    onEnterCodeFragmentListener EnterCodeFragmentListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            EnterCodeFragmentListener = (onEnterCodeFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onSomeEventListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEnterCodeBinding.inflate(getLayoutInflater());
        binding.ContinueBtnCode.setOnClickListener(v -> EnterCodeFragmentListener.ContinueBtnCode(binding.AuthCodeTextEdit.getText().toString()));


        return binding.getRoot();
    }
}