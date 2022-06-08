package com.samsungschool.umbrellaproject.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.samsungschool.umbrellaproject.databinding.FragmentRegistrateNewUserBinding;

public class RegistrateNewUser extends Fragment {

    private FragmentRegistrateNewUserBinding binding;
    private EnterUserData EnterUserDataListener;

    public static RegistrateNewUser newInstance() {
        RegistrateNewUser fragment = new RegistrateNewUser();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            EnterUserDataListener = (EnterUserData) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onSomeEventListener");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegistrateNewUserBinding.inflate(getLayoutInflater());
        binding.continueBtn.setOnClickListener(v ->{
            String userName = binding.userName.getText().toString();
            String userMail = binding.userMail.getText().toString();
            EnterUserDataListener.userData(userName, userMail);
        });
        return binding.getRoot();
    }

    public interface EnterUserData {
        void userData(String name, String mail);
    }
}