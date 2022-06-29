package com.samsungschool.umbrellaproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.samsungschool.umbrellaproject.databinding.FragmentRegisterBinding;
import com.samsungschool.umbrellaproject.interfaces.AuthListener;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;

    public static String TAG_REGISTER_FRAGMENT = "register";

    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(getLayoutInflater());
        binding.continueBtn.setOnClickListener(v ->{
            String userName = binding.userName.getText().toString();
            String userMail = binding.userMail.getText().toString();
            ((AuthListener) requireActivity()).onInfoSubmitted(userName, userMail);
        });

        return binding.getRoot();
    }
}
